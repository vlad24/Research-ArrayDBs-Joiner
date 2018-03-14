package com.tfpower.arraydbs.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;

import static com.tfpower.arraydbs.entity.TraverseHelper.Status.*;
import static java.util.stream.Collectors.*;

public class GenericGraph {

    private final static Logger logger = LoggerFactory.getLogger(GenericGraph.class);

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
        vertexIndex.put(vertex.getId(), vertex);
    }

    public void addEdge(Edge edge) {
        if (!isEdgeAllowed(edge)){
            throw new IllegalArgumentException("Unknown vertices being connected");
        }
        edges.add(edge);
        incidenceMap.putIfAbsent(edge.getStart(), new HashSet<>());
        incidenceMap.putIfAbsent(edge.getEnd(), new HashSet<>());
        incidenceMap.get(edge.getStart()).add(edge);
        incidenceMap.get(edge.getEnd()).add(edge);
    }

    protected boolean isEdgeAllowed(Edge edge) {
        boolean startKnown = vertices.stream().anyMatch(vertex -> vertex.getId().equals(edge.getStart()));
        boolean endKnown = vertices.stream().anyMatch(vertex -> vertex.getId().equals(edge.getEnd()));
        return startKnown && endKnown;
    }

    public Optional<Vertex> getVertex(String id) {
        return Optional.ofNullable(vertexIndex.get(id));
    }

    public Vertex getExistingVertex(String id){
        return getVertex(id)
                .orElseThrow(() -> new IllegalArgumentException("No vertex with id " + id + " found among: " + vertices));
    }

    public int getVertexAmount() {
        return vertices.size();
    }

    /**
     *
     * @param vertices
     * @return
     */
    public Set<Vertex> getVertexSurrounding(Set<Vertex> vertices) {
        Set<Vertex> surroundings = new HashSet<>();
        for (Vertex vertex: vertices){
            surroundings.addAll(getNeighbours(vertex));
        }
        surroundings.removeAll(vertices);
        return surroundings;
    }


    public Edge getExistingEdge(Vertex first, Vertex second){
        return getSingleEdgeBetween(first.getId(), second.getId())
                .orElseThrow(() -> new IllegalArgumentException("Edge between " + first + " and " + second + " not found"));
    }

    public Optional<Edge> getSingleEdgeBetween(String firstId, String secondId) {
        return firstId.equals(secondId) ?
                Optional.empty() :
                incidenceMap.get(firstId).stream().filter(edge -> edge.isIncidentTo(secondId)).findAny();
    }

    public Optional<Edge> getSingleEdgeBetween(Vertex first, Vertex second){
        return getSingleEdgeBetween(first.getId(), second.getId());
    }

    public Set<Edge> getAllEdgesBetween(Vertex first, Vertex second) {
        return getAllEdgesBetween(first.getId(), second.getId());
    }

    public Set<Edge> getAllEdgesBetween(String firstId, String secondId) {
        return firstId.equals(secondId) ?
                Collections.emptySet() :
                incidenceMap.get(firstId).stream().filter(edge -> edge.isIncidentTo(secondId)).collect(toSet());
    }

    public Set<Edge> getAllEdges() {
        return edges;
    }

    private Set<String> getAllEdgesIds() {
        return getAllEdges().stream().map(Edge::getId).collect(toSet());
    }

    public Set<Vertex> getNeighbours(String vertexId) {
        return incidenceMap.getOrDefault(vertexId, Collections.emptySet()).stream()
                .map(e -> getExistingVertex(e.endDifferingFrom(vertexId)))
                .collect(toSet());
    }

    public Set<String> getNeighboursIds(String vertexId) {
        return incidenceMap.getOrDefault(vertexId, Collections.emptySet()).stream()
                .map(e -> e.endDifferingFrom(vertexId))
                .collect(toSet());
    }

    public Set<Vertex> getNeighboursThat(Predicate<Vertex> vertexPredicate, String vertexId) {
        return getNeighbours(vertexId).stream().filter(vertexPredicate).collect(toSet());
    }

    /**
     * Get all incident edges to a vertex
     * @param vertexId vertex to examine
     * @return edges incident to the provided vertex
     */
    public Set<String> getEdgeSurrounding(String vertexId) {
        return getEdgeSurrounding(Collections.singleton(getExistingVertex(vertexId))).stream().map(Edge::getId).collect(toSet());
    }


    /**
     * Get all edges that are incident to any of the vertex in the provided vertex set
     * @param vertices vertices to scan edges for around
     * @return set of edges surrounding the vertex set
     */
    public Set<Edge> getEdgeSurrounding(Set<Vertex> vertices) {
        Set<Vertex> vertexSurrounding = getVertexSurrounding(vertices);
        Set<Edge> edgeSurroundings = vertices.stream().map(v -> getEdgesAround(v, vertexSurrounding))
                .reduce(new HashSet<>(), (acc, edges) -> {
                    acc.addAll(edges);
                    return acc;
                });
        edgeSurroundings.removeAll(getAllEdgesWithin(vertices));
        return edgeSurroundings;
    }

    /**
     * Gets all edges for which each nib is contained in vertex set
     * @param vertices vertex set to examine
     * @return edges for which each nib is contained in vertex set
     */
    private Set<Edge> getAllEdgesWithin(Set<Vertex> vertices) {
        Set<Edge> result = new HashSet<>(vertices.size() * vertices.size());
        for (Vertex vertex : vertices) {
            for (Vertex otherVertex : vertices) {
                if (!vertex.equals(otherVertex)) {
                    result.addAll(getAllEdgesBetween(vertex, otherVertex));
                }
            }
        }
        return result;
    }


    /**
     * Given an anchor vertex and surroundings returns edges that connect vertex with surroundings
     * @param anchorVertex vertex around which to scan edges for
     * @param surroundingVertices vertex to which scan edges for
     * @return  edges that connect vertex with surroundings
     */
    public Set<Edge> getEdgesAround(Vertex anchorVertex, Set<Vertex> surroundingVertices) {
        Set<Edge> set = new HashSet<>();
        for (Vertex v : surroundingVertices) {
            Optional<Edge> edgeBetween = getSingleEdgeBetween(anchorVertex, v);
            if (edgeBetween.isPresent()) {
                Edge edge = edgeBetween.get();
                set.add(edge);
            }
        }
        return set;
    }

    public Set<Vertex> getNeighbours(Vertex vertex){
        return getNeighbours(vertex.getId());
    }

    public Set<Vertex> getNeighboursThat(Predicate<Vertex> vertexPredicate, Vertex vertex){
        return getNeighboursThat(vertexPredicate, vertex.getId());
    }

    public int getEdgeAmount() {
        return edges.size();
    }

    public Map<String, Set<Edge>> getIncidenceMap() {
        return incidenceMap;
    }

    public Set<Vertex> getAllVertices() {
        return vertices;
    }

    public Set<String> getAllVerticesIds() {
        return vertices.stream().map(Vertex::getId).collect(toSet());
    }

    public int degree(Vertex vertex){
        return degree(vertex.getId());
    }

    public int degree(String vertexId) {
        return incidenceMap.getOrDefault(vertexId, Collections.emptySet()).size();
    }

    public Optional<Queue<Vertex>> getPathBetween(Vertex first, Vertex second){
        return getPathBetween(first.getId(), second.getId());
    }

    public Optional<Queue<Vertex>> getPathBetween(String firstId, String secondId) {
        return searchPathBetween(secondId, firstId, new TraverseHelper());
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

    public Boolean areConnected(String leftId, String rightId){
        return getPathBetween(leftId, rightId).isPresent();
    }

    public Boolean areConnected(Vertex leftVertex, Vertex rightVertex){
        return areConnected(leftVertex.getId(), rightVertex.getId());
    }

    public Map<String, Set<String>> computeEdgeSets(int level) {
        Set<String> allVerticesIds = getAllVerticesIds();
        Map<String, Set<String>> result = new HashMap<>(allVerticesIds.size());
        if (level == 0){
            allVerticesIds.forEach(v -> result.put(v, Collections.emptySet()));
        } else if (level >= getEdgeAmount()){
            allVerticesIds.forEach(v -> getAllEdgesIds());
        } else {
            Map<String, List<Set<String>>> edgeSetsMap = new HashMap<>();
            for (String vertexId : allVerticesIds) {
                List<Set<String>> initial = new ArrayList<>(Collections.nCopies(level, null));
                initial.set(0, getEdgeSurrounding(vertexId));
                edgeSetsMap.put(vertexId, initial);
            }
            for (String vertexId : allVerticesIds) {
                computeEdgeSetsHelper(vertexId, new HashSet<>(), level, edgeSetsMap);
            }
            edgeSetsMap.forEach((key, value) -> result.put(key, value.get(level - 1)));
        }
        return result;
    }

    private Set<String> computeEdgeSetsHelper(String current, Set<String> visitedVertices, int level, Map<String, List<Set<String>>> edgeBuffer) {
        Set<String> edgeSet = new HashSet<>();
        Set<String> neighbourIds = getNeighboursIds(current);
        visitedVertices.add(current);
        if (level == 1){
            edgeSet.addAll(neighbourIds);
            return edgeSet;
        } else {
            edgeSet.addAll(getEdgeSurrounding(current));
            for (String neighbourId: neighbourIds){
                if (!visitedVertices.contains(neighbourId)) {
                    Set<String> computedNeighbourEdges = edgeBuffer.get(neighbourId).get(level - 2);
                    if (edgeBuffer.containsKey(neighbourId) && computedNeighbourEdges != null) {
                        edgeSet.addAll(computedNeighbourEdges);
                    } else {
                        edgeSet.addAll(computeReachSetsHelper(neighbourId, visitedVertices, level - 1, edgeBuffer));
                    }
                }
            }
            edgeBuffer.get(current).set(level - 1, edgeSet);
            logger.debug("Setting {} level of {} to {}", level, current, edgeSet);
            return edgeSet;
        }
    }


    public Map<String, Set<String>> computeReachSets(int level) {
        Set<String> allVerticesIds = getAllVerticesIds();
        Map<String, Set<String>> result = new HashMap<>(allVerticesIds.size());
        if (level == 0){
            allVerticesIds.forEach(v -> result.put(v, Collections.singleton(v)));
        } else if (level >= getEdgeAmount()){
            allVerticesIds.forEach(v -> { HashSet<String> allButV = new HashSet<>(allVerticesIds); allButV.remove(v); result.put(v, allButV);});
        } else {
            Map<String, List<Set<String>>> reachSetsMap = new HashMap<>();
            for (String vertexId : allVerticesIds) {
                List<Set<String>> initial = new ArrayList<>(Collections.nCopies(level, null));
                initial.set(0, getNeighboursIds(vertexId));
                reachSetsMap.put(vertexId, initial);
            }
            for (String vertexId : allVerticesIds) {
                computeReachSetsHelper(vertexId, new HashSet<>(Collections.singleton(vertexId)), level, reachSetsMap);
            }
            reachSetsMap.forEach((key, value) -> result.put(key, value.get(level - 1)));
        }
        return result;
    }

    private Set<String> computeReachSetsHelper(String current, Set<String> ignored, int level, Map<String, List<Set<String>>> reachSetsBuffer) {
        Set<String> reachSet = new HashSet<>();
        Set<String> neighbourIds = getNeighboursIds(current);
        if (level == 1){
            reachSet.addAll(neighbourIds);
            reachSet.removeAll(ignored);
            return reachSet;
        } else {
            reachSet.addAll(neighbourIds);
            for (String neighbourId: neighbourIds){
                if (!ignored.contains(neighbourId)) {
                    Set<String> alreadyComputedValues = reachSetsBuffer.get(neighbourId).get(level - 2);
                    if (reachSetsBuffer.containsKey(neighbourId) && alreadyComputedValues != null) {
                        Set<String> valuesToAdd = new HashSet<>(alreadyComputedValues);
                        valuesToAdd.removeAll(ignored);
                        reachSet.addAll(valuesToAdd);
                    } else {
                        HashSet<String> furtherIgnored = new HashSet<>(ignored);
                        furtherIgnored.addAll(neighbourIds);
                        furtherIgnored.add(current);
                        Set<String> reachSetNbrPart = computeReachSetsHelper(neighbourId, furtherIgnored, level - 1, reachSetsBuffer);
                        reachSet.addAll(reachSetNbrPart);
                    }
                }
            }
            reachSetsBuffer.get(current).set(level - 1, reachSet);
            logger.debug("Setting {} level of {} to {}", level, current, reachSet);
            return reachSet;
        }
    }

    public GenericGraph copy() {
        GenericGraph cloneGraph = new GenericGraph();
        vertices.stream().map(Vertex::copy).forEach(cloneGraph::addVertex);
        edges.stream().map(Edge::copy).forEach(cloneGraph::addEdge);
        cloneGraph.vertexIndex = new HashMap<>(this.vertexIndex);
        return cloneGraph;
    }

    @Override
    public String toString() {
        return "GenericGraph{" + "\n\t" +
                " vertices=" + vertices + ",\n\t" +
                " edges=" + edges + ",\n\t" +
                " incidenceMap=" + incidenceMap + "\n" +
                '}';
    }
}
