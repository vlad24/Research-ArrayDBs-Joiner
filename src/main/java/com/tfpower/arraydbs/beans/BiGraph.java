package com.tfpower.arraydbs.beans;

import com.tfpower.arraydbs.domain.Edge;
import com.tfpower.arraydbs.domain.Vertex;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by vlad on 21.02.18.
 */
public interface BiGraph {

    void addFirstClassVertex(Vertex vertex);

    void addSecondClassVertex(Vertex vertex);

    void addEdge(int vertexEndId, int vertexStartId);

    Set<Vertex> getLeftVertices();

    Set<Vertex> getRightVertices();

    List<Edge> getEdges();

    Optional<Vertex> getVertexById(Integer id);

    Optional<Edge> getEdgeBetween(Integer leftId, Integer rightId);

    Map<Integer, List<Vertex>> getNeighbourMap();

    Optional<List<Vertex>> getPathBetween(Vertex first, Vertex second);

    Optional<List<BiGraph>> getConnectedComponents();

    default Optional<Edge> getEdgeBetween(Vertex first, Vertex second){
        return getEdgeBetween(first.getId(), second.getId());
    }

    default Boolean areConnected(Integer a, Integer b){
        return getEdgeBetween(a, b).isPresent();
    }

    default Boolean areConnected(Vertex a, Vertex b){
        return areConnected(a.getId(), b.getId());
    }

    default void addEdge(Vertex vertexStart, Vertex vertexEnd){
        addEdge(vertexStart.getId(), vertexEnd.getId());
    }
}
