package com.tfpower.arraydbs.beans.impl;

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
public abstract class ArrayJoinerCacheHeuristicsImplBase implements ArrayJoiner {

    private final static Logger logger = LoggerFactory.getLogger(ArrayJoinerCacheHeuristicsImplBase.class);

    @Autowired
    protected Cache<Vertex> cache;

    public JoinReport join(BiGraph bGraph) {
        logger.info("{} is joining {}", this.toString(), bGraph.getName());
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
            logger.trace("Processing: {}", currentVertex);
            traverse.markVertex(currentVertex, DONE);
            traverse.updateCounterAfterVisit(currentVertex);
            traverse.pushToVisitResult(currentVertex);
            traverse.updateAccumulatorBy(currentVertex);
            cache.loadOrFail(currentVertex);
            logger.trace("Cache has been updated by {}.\tCurrent: {}", currentVertex, cache);
            Set<Edge> edgesInCache = bGraph.getEdgesAround(currentVertex, cache.getAllValues());
            logger.trace("Processing edges that cache allows: {}", edgesInCache);
            edgesInCache.forEach(e -> traverse.markEdge(e, DONE));
            logger.trace("Edge status:   {}", traverse.getEdgeStatus());
            logger.trace("Vertex status: {}", traverse.getVertexStatus());
            logger.trace("Visit result:  {}", traverse.getVisitResult());
            Optional<Vertex> nextVertex = pickNext(currentVertex, bGraph, traverse);
            if (nextVertex.isPresent()) {
                logger.trace("Vertex {} will be visited next...", nextVertex);
                if (cache.getAllValues().size() == cache.getCapacity()) {
                    Vertex evicted = cache.evict(
                            comparing((Cache.CacheEntry<Vertex> v) ->
                                    bGraph.areDirectlyConnected(v.getValue(), nextVertex.get()) ? 0 : 1)
                                    .thenComparing(vertex -> -uDegree(bGraph, traverse, vertex.getValue()))
                    );
                    logger.trace("Evicted {} to free up space for next vertex...", evicted);
                }
                currentVertex = nextVertex.get();
                traverse.finishIf(processedEdges == edgesAmount);
                processedEdges = traverse.countEdgesMarked(DONE);
                logger.trace("Edges left to process: {}", edgesAmount - processedEdges);
            } else {
                logger.trace("Finishing as there are no more next vertices");
                traverse.finish();
            }
        }
        while (traverse.isNotFinished());

        Set<Edge> b = bGraph.getAllEdges().stream().filter(e -> traverse.statusOfEdge(e) != DONE).collect(toSet());
        assert b.size() == 0 : "Not all edges were processed : " + b;
        return JoinReport.fromGraphTraversal(traverse, this.toString(), bGraph.description());
    }


    protected Vertex pickFirstVertex(BiGraph biGraph) {
        return biGraph.getAllVerticesIds().stream()
                .map(biGraph::getExistingVertex)
                .min(Comparator.comparingInt((ToIntFunction<Vertex>) biGraph::degree)
                        .thenComparing(vertex -> -vertex.getWeight()))
                .orElseThrow(() -> new IllegalStateException("No min degree vertex found"));
    }

    protected abstract Optional<Vertex> pickNext(Vertex current, BiGraph bGraph, TraverseHelper traverse);

    protected Integer uDegree(BiGraph bGraph, TraverseHelper traverseHelper, Vertex vertex) {
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
