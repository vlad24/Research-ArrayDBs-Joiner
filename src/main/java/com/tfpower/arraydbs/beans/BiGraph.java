package com.tfpower.arraydbs.beans;

import com.tfpower.arraydbs.domain.Edge;
import com.tfpower.arraydbs.domain.Vertex;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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

    Set<Vertex> getNeighboursOf(Integer vertex);

    Optional<Vertex> getVertexById(Integer id);

    Optional<Edge> getEdgeBetween(Integer leftId, Integer rightId);

    Map<Integer, Set<Edge>> getIncidenceMap();

    Optional<Queue<Vertex>> getPathBetween(Integer firstId, Integer secondId);


    default Optional<Queue<Vertex>> getPathBetween(Vertex first, Vertex second){
        return getPathBetween(first.getId(), second.getId());
    }

    default Optional<Edge> getEdgeBetween(Vertex first, Vertex second){
        return getEdgeBetween(first.getId(), second.getId());
    }

    default Boolean areConnected(Integer leftId, Integer rightId){
        return getPathBetween(leftId, rightId).isPresent();
    }

    default Boolean areConnected(Vertex a, Vertex b){
        return areConnected(a.getId(), b.getId());
    }

    default Vertex getVertexByIdOrFail(Integer id){
        return getVertexById(id).orElseThrow(() -> new IllegalArgumentException("No vertex with id " + id + " found"));
    }

    default Set<Vertex> getNeighboursOf(Vertex vertex){
        return getNeighboursOf(vertex.getId());
    }


}
