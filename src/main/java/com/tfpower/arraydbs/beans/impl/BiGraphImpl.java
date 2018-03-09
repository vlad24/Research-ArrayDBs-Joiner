package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.BiGraph;
import com.tfpower.arraydbs.domain.Edge;
import com.tfpower.arraydbs.domain.TraverseHelper;
import com.tfpower.arraydbs.domain.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.function.Predicate;

import static com.tfpower.arraydbs.domain.TraverseHelper.Status.*;
import static java.util.stream.Collectors.toSet;

/**
 * Created by vlad on 21.02.18.
 */
@Component
public class BiGraphImpl implements BiGraph {

    @Autowired
    BiGraphFileIncListParser parser;

    private Set<String> leftVertices;
    private Set<String> rightVertices;
    private Set<Edge> edges;
    private Map<String, Vertex> vertexIndex;
    private Map<String, Set<Edge>> incidenceMap;

    public BiGraphImpl(){
        vertexIndex = new HashMap<>();
        incidenceMap = new HashMap<>();
        rightVertices = new HashSet<>();
        leftVertices = new HashSet<>();
        edges = new HashSet<>();
        vertexIndex = new HashMap<>();
    }

    @PostConstruct
    private void initGraphFromFile() throws IOException, ParseException {
        parser.buildFromFile(this);
    }

    @Override
    public void addFirstClassVertex(Vertex vertex) {
        leftVertices.add(vertex.getId());
        vertexIndex.put(vertex.getId(), vertex);
    }

    @Override
    public void addSecondClassVertex(Vertex vertex) {
        rightVertices.add(vertex.getId());
        vertexIndex.put(vertex.getId(), vertex);
    }

    @Override
    public void addEdge(Edge edge) {
        edges.add(edge);
        incidenceMap.putIfAbsent(edge.getStart(), new HashSet<>());
        incidenceMap.putIfAbsent(edge.getEnd(), new HashSet<>());
        incidenceMap.get(edge.getStart()).add(edge);
        incidenceMap.get(edge.getEnd()).add(edge);
    }

    @Override
    public Set<Vertex> getLeftVertices() {
        return vertexIndex.entrySet().stream()
                .filter(e -> leftVertices.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .collect(toSet());
    }

    @Override
    public Set<Vertex> getRightVertices() {
        return vertexIndex.entrySet().stream()
                .filter(e -> rightVertices.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .collect(toSet());
    }

    @Override
    public Set<Edge> getEdges() {
        return edges;
    }

    @Override
    public Set<Vertex> getNeighbours(String vertexId) {
        return incidenceMap.getOrDefault(vertexId, Collections.emptySet()).stream()
                .map(e -> getVertexByIdOrFail(e.endDifferingFrom(vertexId)))
                .collect(toSet());
    }

    @Override
    public Set<Vertex> getNeighboursThat(String vertexId, Predicate<Vertex> vertexPredicate) {
        return getNeighbours(vertexId).stream().filter(vertexPredicate).collect(toSet());
    }

    @Override
    public Set<Vertex> getSurroundingsOf(Set<Vertex> vertices) {
        HashSet<Vertex> surroundings = new HashSet<>();
        for (Vertex vertex: vertices){
            surroundings.addAll(getNeighbours(vertex));
        }
        surroundings.removeAll(vertices);
        return surroundings;
    }

    @Override
    public Optional<Vertex> getVertexById(String id) {
        return Optional.ofNullable(vertexIndex.get(id));
    }

    @Override
    public Optional<Edge> getEdgeBetween(String firstId, String secondId) {
        return firstId.equals(secondId) ?
                Optional.empty() :
                incidenceMap.get(firstId).stream().filter(edge -> edge.isIncidentTo(secondId)).findAny();
    }

    @Override
    public Map<String, Set<Edge>> getIncidenceMap() {
        return incidenceMap;
    }

    @Override
    public Optional<Queue<Vertex>> getPathBetween(String firstId, String secondId) {
        return searchPathBetween(secondId, firstId, new TraverseHelper());
    }

    @Override
    public Set<String> getAllVerticesIds() {
        Set<String> allVertices = new HashSet<>(getVertexAmount());
        allVertices.addAll(leftVertices);
        allVertices.addAll(rightVertices);
        return allVertices;
    }

    @Override
    public int getVertexAmount() {
        return rightVertices.size() + leftVertices.size();
    }

    @Override
    public int getEdgeAmount() {
        return edges.size();
    }

    @Override
    public Set<Edge> getEdgesBetween(Vertex anchorVertex, Set<Vertex> surroundingVertices) {
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

    @Override
    public int degree(String vertexId) {
        return incidenceMap.getOrDefault(vertexId, Collections.emptySet()).size();
    }

    private Optional<Queue<Vertex>> searchPathBetween(String destinationVertexId, String current, TraverseHelper traversal) {
        if (current.equals(destinationVertexId)) {
            traversal.finish();
            return Optional.of(traversal.getVisitHistory());
        } else {
            traversal.pushToVisitPath(getVertexByIdOrFail(current));
            traversal.markVertex(current, IN_PROGRESS);
            Set<Vertex> unvisitedNeighbours = getNeighbours(current)
                    .stream().filter(n -> traversal.statusOfVertex(n) == UNTOUCHED).collect(toSet());
            if (unvisitedNeighbours.isEmpty()) {
                traversal.markVertex(current, DONE);
            } else {
                Iterator<Vertex> iterator = unvisitedNeighbours.iterator();
                while (iterator.hasNext() && !traversal.isFinished()) {
                    String neighbourId = iterator.next().getId();
                    searchPathBetween(destinationVertexId, neighbourId, traversal);
                    if (!traversal.isFinished()) {
                        traversal.popFromVisitPath();
                    }
                }
            }
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return "BiGraphImpl{" + "\n" +
                "   leftVertices=" + leftVertices + ",\n," +
                "   rightVertices=" + rightVertices + ",\n" +
                "   edges=" + edges + "\n" +
                '}';
    }

}
