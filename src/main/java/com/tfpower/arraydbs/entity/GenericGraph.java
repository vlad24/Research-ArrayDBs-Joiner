package com.tfpower.arraydbs.entity;

import java.util.*;
import java.util.function.Predicate;

import static com.tfpower.arraydbs.entity.TraverseHelper.Status.*;
import static java.util.stream.Collectors.toSet;

public class GenericGraph {

    protected Set<Vertex> vertices;
    protected Set<Edge> edges;
    protected Map<String, Vertex> vertexIndex;
    protected Map<String, Set<Edge>> incidenceMap;

    public GenericGraph() {
        vertices = new HashSet<>();
        vertexIndex = new HashMap<>();
        incidenceMap = new HashMap<>();
        edges = new HashSet<>();
        vertexIndex = new HashMap<>();
    }

    public void addVertex(Vertex vertex){
        vertices.add(vertex);
    }

    public void addEdge(Edge edge) {
        if (!isAllowed(edge)){
            throw new IllegalArgumentException("Unknown vertices being connected");
        }
        edges.add(edge);
        incidenceMap.putIfAbsent(edge.getStart(), new HashSet<>());
        incidenceMap.putIfAbsent(edge.getEnd(), new HashSet<>());
        incidenceMap.get(edge.getStart()).add(edge);
        incidenceMap.get(edge.getEnd()).add(edge);
    }

    protected boolean isAllowed(Edge edge) {
        boolean startKnown = vertices.stream().anyMatch(vertex -> vertex.getId().equals(edge.getStart()));
        boolean endKnown = vertices.stream().anyMatch(vertex -> vertex.getId().equals(edge.getEnd()));
        return startKnown && endKnown;
    }

    public Set<Edge> getAllEdges() {
        return edges;
    }

    public Set<Vertex> getNeighbours(String vertexId) {
        return incidenceMap.getOrDefault(vertexId, Collections.emptySet()).stream()
                .map(e -> getExistingVertex(e.endDifferingFrom(vertexId)))
                .collect(toSet());
    }

    public Set<Vertex> getNeighboursThat(Predicate<Vertex> vertexPredicate, String vertexId) {
        return getNeighbours(vertexId).stream().filter(vertexPredicate).collect(toSet());
    }

    public Set<Vertex> getSurroundingsOf(Set<Vertex> vertices) {
        HashSet<Vertex> surroundings = new HashSet<>();
        for (Vertex vertex: vertices){
            surroundings.addAll(getNeighbours(vertex));
        }
        surroundings.removeAll(vertices);
        return surroundings;
    }

    public Optional<Vertex> getVertexById(String id) {
        return Optional.ofNullable(vertexIndex.get(id));
    }

    public Optional<Edge> getEdgeBetween(String firstId, String secondId) {
        return firstId.equals(secondId) ?
                Optional.empty() :
                incidenceMap.get(firstId).stream().filter(edge -> edge.isIncidentTo(secondId)).findAny();
    }

    public Set<Edge> getAllEdgesBetween(Vertex first, Vertex second) {
        return getAllEdgesBetween(first.getId(), second.getId());
    }

    public Set<Edge> getAllEdgesBetween(String firstId, String secondId) {
        return firstId.equals(secondId) ?
                Collections.emptySet() :
                incidenceMap.get(firstId).stream().filter(edge -> edge.isIncidentTo(secondId)).collect(toSet());
    }

    public Map<String, Set<Edge>> getIncidenceMap() {
        return incidenceMap;
    }

    public Optional<Queue<Vertex>> getPathBetween(String firstId, String secondId) {
        return searchPathBetween(secondId, firstId, new TraverseHelper());
    }

    public Set<Vertex> getAllVertices() {
        return vertices;
    }

    public Set<String> getAllVerticesIds() {
        return vertices.stream().map(Vertex::getId).collect(toSet());
    }

    public int getVertexAmount() {
        return vertices.size();
    }

    public int getEdgeAmount() {
        return edges.size();
    }

    public Set<Edge> getEdgesAround(Vertex anchorVertex, Set<Vertex> surroundingVertices) {
        Set<Edge> set = new HashSet<>();
        for (Vertex v : surroundingVertices) {
            Optional<Edge> edgeBetween = getEdgeBetween(anchorVertex, v);
            if (edgeBetween.isPresent()) {
                Edge edge = edgeBetween.get();
                set.add(edge);
            }
        }
        return set;
    }

    public int degree(String vertexId) {
        return incidenceMap.getOrDefault(vertexId, Collections.emptySet()).size();
    }

    private Optional<Queue<Vertex>> searchPathBetween(String destinationVertexId, String current, TraverseHelper traversal) {
        if (current.equals(destinationVertexId)) {
            traversal.finish();
            return Optional.of(traversal.getVisitResult());
        } else {
            traversal.pushToVisitResult(getExistingVertex(current));
            traversal.markVertex(current, IN_PROGRESS);
            Set<Vertex> unvisitedNeighbours = getNeighbours(current)
                    .stream().filter(n -> traversal.statusOfVertex(n) == UNTOUCHED).collect(toSet());
            if (unvisitedNeighbours.isEmpty()) {
                traversal.markVertex(current, DONE);
            } else {
                Iterator<Vertex> iterator = unvisitedNeighbours.iterator();
                while (iterator.hasNext() && traversal.isNotFinished()) {
                    String neighbourId = iterator.next().getId();
                    searchPathBetween(destinationVertexId, neighbourId, traversal);
                    if (traversal.isNotFinished()) {
                        traversal.popFromVisitResult();
                    }
                }
            }
            return Optional.empty();
        }
    }

    public Edge getExistingEdge(Vertex first, Vertex second){
        return getEdgeBetween(first.getId(), second.getId())
                .orElseThrow(() -> new IllegalArgumentException("Edge between " + first + " and " + second + " not found"));
    }

    public Optional<Queue<Vertex>> getPathBetween(Vertex first, Vertex second){
        return getPathBetween(first.getId(), second.getId());
    }

    public Optional<Edge> getEdgeBetween(Vertex first, Vertex second){
        return getEdgeBetween(first.getId(), second.getId());
    }

    public Boolean areConnected(String leftId, String rightId){
        return getPathBetween(leftId, rightId).isPresent();
    }

    public Boolean areConnected(Vertex a, Vertex b){
        return areConnected(a.getId(), b.getId());
    }

    public Vertex getExistingVertex(String id){
        return getVertexById(id)
                .orElseThrow(() -> new IllegalArgumentException("No vertex with id " + id + " found among: " + vertices));
    }

    public Set<Vertex> getNeighbours(Vertex vertex){
        return getNeighbours(vertex.getId());
    }

    public Set<Vertex> getNeighboursThat(Predicate<Vertex> vertexPredicate, Vertex vertex){
        return getNeighboursThat(vertexPredicate, vertex.getId());
    }

    public int degree(Vertex vertex){
        return degree(vertex.getId());
    }

    public GenericGraph copy() {
        GenericGraph cloneGraph = new GenericGraph();
        vertices.stream().map(Vertex::copy).forEach(cloneGraph::addVertex);
        edges.stream().map(Edge::copy).forEach(cloneGraph::addEdge);
        cloneGraph.vertexIndex = new HashMap<>(this.vertexIndex);
        return cloneGraph;
    }
}
