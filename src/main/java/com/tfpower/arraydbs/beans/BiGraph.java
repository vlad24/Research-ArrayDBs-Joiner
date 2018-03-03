package com.tfpower.arraydbs.beans;

import com.tfpower.arraydbs.domain.Edge;
import com.tfpower.arraydbs.domain.Vertex;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by vlad on 21.02.18.
 */
public interface BiGraph {

    void addFirstClassVertex(Vertex vertex);

    void addSecondClassVertex(Vertex vertex);

    void addEdge(Edge edge);

    Set<Vertex> getLeftVertices();

    Set<Vertex> getRightVertices();

    Set<Edge> getEdges();

    Set<Vertex> getNeighbours(String vertex);

    Set<Vertex> getNeighboursThat(String vertexId, Predicate<Vertex> vertexPredicate);

    Set<Vertex> getNeighbours(Set<Vertex> vertices);

    Optional<Vertex> getVertexById(String id);

    Optional<Edge> getEdgeBetween(String firstId, String leftId);

    Map<String, Set<Edge>> getIncidenceMap();

    Optional<Queue<Vertex>> getPathBetween(String firstId, String secondId);

    Set<String> getAllVertices();

    int getVertexAmount();

    int getEdgeAmount();

    Set<Edge> getEdgesBetween(Vertex nextVertex, Set<Vertex> allEntries);

    int degree(String vertexId);

    default Edge getEdgeBetweenOrFail(Vertex first, Vertex second){
        return getEdgeBetween(first.getId(), second.getId())
                .orElseThrow(() -> new IllegalArgumentException("Edge between " + first + " and " + second + " not found"));
    }

    default Optional<Queue<Vertex>> getPathBetween(Vertex first, Vertex second){
        return getPathBetween(first.getId(), second.getId());
    }

    default Optional<Edge> getEdgeBetween(Vertex first, Vertex second){
        return getEdgeBetween(first.getId(), second.getId());
    }

    default Boolean areConnected(String leftId, String rightId){
        return getPathBetween(leftId, rightId).isPresent();
    }

    default Boolean areConnected(Vertex a, Vertex b){
        return areConnected(a.getId(), b.getId());
    }

    default Vertex getVertexByIdOrFail(String id){
        return getVertexById(id).orElseThrow(() -> new IllegalArgumentException("No vertex with id " + id + " found"));
    }

    default Set<Vertex> getNeighbours(Vertex vertex){
        return getNeighbours(vertex.getId());
    }

    default Set<Vertex> getNeighboursThat(Vertex vertex, Predicate<Vertex> vertexPredicate){
        return getNeighboursThat(vertex.getId(), vertexPredicate);
    }

    default int degree(Vertex vertex){
        return degree(vertex.getId());
    }

}
