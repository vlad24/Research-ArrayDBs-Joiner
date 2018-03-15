package com.tfpower.arraydbs.beans.impl;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.Cache;
import com.tfpower.arraydbs.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.ToIntFunction;

import static com.tfpower.arraydbs.entity.TraverseHelper.Status.DONE;
import static com.tfpower.arraydbs.entity.TraverseHelper.Status.UNTOUCHED;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;

/**
 * Created by vlad on 24.01.18.
 */
@Component
@Primary
public class ArrayJoinerCacheHeuristicsImpl implements ArrayJoiner {

    private final static Logger logger = LoggerFactory.getLogger(ArrayJoinerCacheHeuristicsImpl.class);

    @Autowired
    Cache<Vertex> cache;

    public JoinReport join(BiGraph bGraph) {
        final Set<String> allVertices = bGraph.getAllVerticesIds();
        final TraverseHelper traverse = new TraverseHelper();
        traverse.markVertices(allVertices, UNTOUCHED);
        traverse.setAccumulatorUpdater((acc, vertex) -> acc + vertex.getWeight());
        Vertex currentVertex = pickFirstVertex(bGraph);
        int iterationNumber = 0;
        int processedEdges = 0;
        int edgesAmount = bGraph.getEdgeAmount();
        cache.clear();
        do {
            iterationNumber++;
            logger.trace("Iteration: {}", iterationNumber);
            logger.debug("Processing: {}", currentVertex);
            traverse.markVertex(currentVertex, DONE);
            traverse.accountVisit(currentVertex);
            traverse.pushToVisitResult(currentVertex);
            traverse.updateAccumulatorBy(currentVertex);
            cache.loadOrFail(currentVertex);
            logger.debug("Cache has been updated by {}. Current: {}", currentVertex, cache);
            Set<Edge> edgesInCache = bGraph.getEdgesAround(currentVertex, cache.getAllValues());
            logger.debug("Processing edges that cache allows: {}", edgesInCache);
            edgesInCache.forEach(e -> traverse.markEdge(e, DONE));
            logger.debug("Edge status: {}", traverse.getEdgeStatus());
            logger.debug("Vertex status: {}", traverse.getVertexStatus());
            logger.debug("Visit result: {}", traverse.getVisitResult());
            Optional<Vertex> nextVertex = pickNext(currentVertex, bGraph, traverse);
            if (nextVertex.isPresent()) {
                logger.debug("Vertex {} will be visited next...", nextVertex);
                if (cache.getAllValues().size() == cache.getCapacity()) {
                    Vertex evicted = cache.evict(
                            comparing((Cache.CacheEntry<Vertex> v) ->
                                    bGraph.areDirectlyConnected(v.getValue(), nextVertex.get()) ? 0 : 1)
                                    .thenComparing(vertex -> -degreeExcludingDone(bGraph, traverse, vertex.getValue()))
                    );
                    logger.debug("Evicted {} to free up space for next vertex...", evicted);
                }
                currentVertex = nextVertex.get();
                traverse.finishIf(processedEdges == edgesAmount);
                processedEdges = traverse.countEdgesMarked(DONE);
                logger.debug("Edges left to process: {}", edgesAmount - processedEdges);
            } else {
                traverse.finish();
            }
        }
        while (traverse.isNotFinished());
        return JoinReport.fromGraphTraversal(traverse, this.toString(), bGraph.description());
    }


    private Vertex pickFirstVertex(BiGraph biGraph) {
//        return Randomizer.pickRandomFrom(vertices);
        return biGraph.getAllVerticesIds().stream()
                .map(biGraph::getExistingVertex)
                .min(Comparator.comparingInt((ToIntFunction<Vertex>) biGraph::degree)
                        .thenComparing(Vertex::getWeight))
                .orElseThrow(() -> new IllegalStateException("No min degree vertex found"));
    }

    private Optional<Vertex> pickNext(Vertex current, BiGraph bGraph, TraverseHelper traverse) {
        Set<Vertex> anchorVertices = cache.getAllValues();
        assert anchorVertices.contains(current);
        Set<Vertex> candidateVertices = bGraph.getEdgeSurrounding(anchorVertices).stream()
                .filter(e -> traverse.statusOfEdge(e) != DONE)  // remove all done edges
                .map(e -> anchorVertices.contains(bGraph.getExistingVertex(e.getStart())) ? e.getEnd() : e.getStart()) // get only outer vertices
                .map(bGraph::getExistingVertex) //map to vertex objects
                .collect(toSet());
        return candidateVertices.stream()
                .min(comparing(traverse::statusOfVertex)                                              // first pick untouched ones
                        .thenComparing(neighbour -> degreeExcludingDone(bGraph, traverse, neighbour)) // then min by done-degree
                        .thenComparing(neighbour -> -neighbour.getWeight())                           // then the most light one
                );

    }

    private Integer degreeExcludingDone(BiGraph bGraph, TraverseHelper traverseHelper, Vertex vertex) {
        Set<Vertex> neighborsByDoneEdges = bGraph.getNeighboursThat(
                nbr -> traverseHelper.statusOfEdge(bGraph.getExistingEdge(nbr, vertex)) == DONE,
                vertex
        );
        return bGraph.degree(vertex) - neighborsByDoneEdges.size();
    }


    @Override
    public String toString() {
        return "cacheHeuristic-joiner<" + cache.getCapacity() + ">";
    }
}
