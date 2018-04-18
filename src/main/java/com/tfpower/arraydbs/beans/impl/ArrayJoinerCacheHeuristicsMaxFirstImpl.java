package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.entity.BiGraph;
import com.tfpower.arraydbs.entity.Edge;
import com.tfpower.arraydbs.entity.TraverseHelper;
import com.tfpower.arraydbs.entity.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.tfpower.arraydbs.entity.TraverseHelper.Status.DONE;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;

/**
 * Created by vlad on 24.01.18.
 */
@Component
@Primary
public class ArrayJoinerCacheHeuristicsMaxFirstImpl extends ArrayJoinerCacheHeuristicsImplBase {

    private final static Logger logger = LoggerFactory.getLogger(ArrayJoinerCacheHeuristicsMaxFirstImpl.class);


    @Override
    protected Optional<Vertex> pickNext(Vertex current, BiGraph bGraph, TraverseHelper traverse) {
        Set<Vertex> anchorVertices = cache.getAllValues();
        assert anchorVertices.contains(current) : "Cache does not contain current vertex";
        Set<Vertex> candidateVertices = bGraph.getEdgeSurrounding(anchorVertices).stream()
                .filter(e -> traverse.statusOfEdge(e) != DONE)                                                         // remove all done edges
                .map(e -> anchorVertices.contains(bGraph.getExistingVertex(e.getStart())) ? e.getEnd() : e.getStart()) // get only outer vertices
                .map(bGraph::getExistingVertex)                                                                        //map to vertex objects
                .collect(toSet());
        if (candidateVertices.isEmpty() && traverse.countEdgesMarked(DONE) != bGraph.getEdgeAmount()){
            candidateVertices = bGraph.getAllEdges().stream()
                    .filter(edge -> traverse.statusOfEdge(edge) != DONE)
                    .map(Edge::nibs)
                    .reduce(new HashSet<>(), (accSet, nibs) -> {
                        accSet.addAll(nibs);
                        return accSet;
                    })
                    .stream().map(bGraph::getExistingVertex).collect(toSet());
        }
        return candidateVertices.stream()
                .min(comparing(traverse::statusOfVertex)                                              // first pick untouched ones
                        .thenComparing(neighbour -> -uDegree(bGraph, traverse, neighbour))            // then max by done-degree
                        .thenComparing(neighbour -> -neighbour.getWeight())                           // then the most light one
                );

    }


    @Override
    public String toString() {
        return "cacheHeuristic-joiner-max<" + cache.getCapacity() + ">";
    }
}
