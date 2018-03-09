package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.BiGraph;
import com.tfpower.arraydbs.beans.Cache;
import com.tfpower.arraydbs.domain.Edge;
import com.tfpower.arraydbs.domain.JoinReport;
import com.tfpower.arraydbs.domain.TraverseHelper;
import com.tfpower.arraydbs.domain.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Set;
import java.util.function.ToIntFunction;

import static com.tfpower.arraydbs.domain.TraverseHelper.Status.DONE;
import static com.tfpower.arraydbs.domain.TraverseHelper.Status.UNTOUCHED;
import static java.util.stream.Collectors.toList;

/**
 * Created by vlad on 24.01.18.
 */
@Component
public class ArrayJoinerWithCacheImpl implements ArrayJoiner {

    private final static Logger logger = LoggerFactory.getLogger(ArrayJoinerWithCacheImpl.class);

    @Autowired
    Cache<Vertex> cache;

    public JoinReport join(BiGraph bGraph) {
        final Set<String> allVertices = bGraph.getAllVerticesIds();
        final JoinReport joinReport = new JoinReport();
        final TraverseHelper traverseHelper = new TraverseHelper();
        traverseHelper.markVertices(allVertices, UNTOUCHED);
        traverseHelper.setAccumulatorUpdater((acc, vertex) -> acc + vertex.getWeight());
        Vertex currentVertex = pickFirstVertex(bGraph);
        int iterationNumber = 0;
        int processedEdges = 0;
        int edgesAmount = bGraph.getEdgeAmount();
        do {
            iterationNumber++;
            logger.trace("Iteration: {}", iterationNumber);
            logger.debug("Processing: {}", currentVertex);
            traverseHelper.markVertex(currentVertex, DONE);
            traverseHelper.accountVisit(currentVertex);
            traverseHelper.pushToVisitPath(currentVertex);
            traverseHelper.updateAccumulatorBy(currentVertex);
            cache.add(currentVertex);
            logger.debug("Cache has been updated by {}. Current: {}", currentVertex, cache);
            Set<Edge> edgesInCache = bGraph.getEdgesBetween(currentVertex, cache.getAllEntries());
            logger.debug("Processing edges that cache allows: {}", edgesInCache);
            edgesInCache.forEach(e -> traverseHelper.markEdge(e, DONE));
            logger.debug("Edge status: {}", traverseHelper.getEdgeStatus());
            logger.debug("Vertex status: {}", traverseHelper.getVertexStatus());
            logger.debug("Visit history: {}", traverseHelper.getVisitHistory());
            Vertex nextVertex = pickNext(currentVertex, bGraph, traverseHelper);
            logger.debug("Vertex {} will be visited next...", nextVertex);
            if (cache.getAllEntries().size() == cache.getCapacity()) {
                Vertex evicted = cache.evict(
                        Comparator.comparing((Vertex v) -> bGraph.getEdgeBetween(v.getId(), nextVertex.getId()).isPresent() ? 0 : 1)
                                .thenComparing(vertex -> -degreeExcludingDone(bGraph, traverseHelper, vertex))
                );
                logger.debug("Evicted {} to free up space for next vertex...", evicted);
            }
            currentVertex = nextVertex;
            processedEdges = traverseHelper.countEdgesMarked(DONE);
            logger.debug("Edges left to process: {}", edgesAmount - processedEdges);
            logger.debug("\n");
        }
        while (processedEdges != edgesAmount);
        joinReport.setLoadFrequencies(traverseHelper.getVisitCountsPerVertices());
        joinReport.setTotalCost(traverseHelper.getAccumulator());
        joinReport.setTraverseSequence(traverseHelper.getVisitHistory().stream().map(Vertex::getId).collect(toList()));
        return joinReport;
    }


    private Vertex pickFirstVertex(BiGraph biGraph) {
//        return Randomizer.pickRandomFrom(vertices);
        return biGraph.getAllVerticesIds().stream()
                .map(biGraph::getVertexByIdOrFail)
                .min(Comparator.comparingInt((ToIntFunction<Vertex>) biGraph::degree)
                        .thenComparing(Vertex::getWeight))
                .orElseThrow(() -> new IllegalStateException("No min degree vertex found"));
    }

    private Vertex pickNext(Vertex current, BiGraph bGraph, TraverseHelper traverseManager) {
        Set<Vertex> anchorVertices = cache.getAllEntries();
        assert anchorVertices.contains(current);
        return bGraph.getSurroundingsOf(anchorVertices).stream()
                .min(Comparator.comparing(traverseManager::statusOfVertex) // first pick untouched ones
                        .thenComparing(neighbour -> degreeExcludingDone(bGraph, traverseManager, neighbour)) // then min by done-degree
                        .thenComparing(neighbour -> -neighbour.getWeight()) // then the most light one
                ).orElse(null);
    }

    private Integer degreeExcludingDone(BiGraph bGraph, TraverseHelper traverseHelper, Vertex vertex) {
        Set<Vertex> neighborsByDoneEdges = bGraph.getNeighboursThat(
                nbr -> traverseHelper.statusOfEdge(bGraph.getEdgeBetweenOrFail(nbr, vertex)) == DONE,
                vertex
        );
        return bGraph.degree(vertex) - neighborsByDoneEdges.size();
    }

}
