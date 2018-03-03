package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.BiGraph;
import com.tfpower.arraydbs.beans.Cache;
import com.tfpower.arraydbs.domain.JoinReport;
import com.tfpower.arraydbs.domain.TraverseHelper;
import com.tfpower.arraydbs.domain.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Set;

import static com.tfpower.arraydbs.domain.TraverseHelper.Status.DONE;
import static com.tfpower.arraydbs.domain.TraverseHelper.Status.UNTOUCHED;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Created by vlad on 24.01.18.
 */
@Component
public class ArrayJoinerWithCacheImpl implements ArrayJoiner {

    private final static Logger logger = LoggerFactory.getLogger(ArrayJoinerWithCacheImpl.class);

    @Autowired
    Cache<Vertex> cache;

    public JoinReport join(BiGraph bGraph) {
        final Set<String> allVertices = bGraph.getAllVertices();
        final int allCapacity = allVertices.size();
        final JoinReport joinReport = new JoinReport();
        final TraverseHelper traverseHelper = new TraverseHelper();
        traverseHelper.markVertices(allVertices, UNTOUCHED);
        traverseHelper.setAccumulatorUpdater((acc, vertex) -> acc + vertex.getWeight());
        Vertex currentVertex = pickFirstVertex(allVertices.stream().map(bGraph::getVertexByIdOrFail).collect(toSet()));
        do {
            logger.debug("Currently at {}. Cache: {}", currentVertex, cache);
            traverseHelper.markVertex(currentVertex, DONE);
            traverseHelper.accountVisit(currentVertex);
            traverseHelper.pushToVisitPath(currentVertex);
            traverseHelper.updateAccumulatorBy(currentVertex);
            Vertex evicted = cache.tryAdd(currentVertex,
                    Comparator.comparing(v -> degreeExcludingDone(bGraph, traverseHelper, v)));
            logger.debug("Evicted: {}", evicted);
            Vertex nextVertex = pickNext(currentVertex, bGraph, traverseHelper);
            bGraph.getEdgesBetween(nextVertex, cache.getAllEntries()).forEach(e -> traverseHelper.markEdge(e, DONE));
            logger.debug("Moved to {}", currentVertex);
            logger.debug("Edge status: {}", traverseHelper.getEdgeStatus());
            logger.debug("Vertex status: {}", traverseHelper.getVertexStatus());
        }
        while (traverseHelper.countVerticesMarked(DONE) != allCapacity);
        joinReport.setLoadFrequencies(traverseHelper.getVisitCountsPerVertices());
        joinReport.setTotalCost(traverseHelper.getAccumulator());
        joinReport.setTraverseSequence(traverseHelper.getVisitHistory().stream().map(Vertex::getId).collect(toList()));
        return joinReport;
    }


    private Vertex pickFirstVertex(Set<Vertex> vertices) {
        return vertices.stream().filter(v -> v.getId().equals("B4")).findFirst().get();
//        return Randomizer.pickRandomFrom(vertices);
    }

    private Vertex pickNext(Vertex current, BiGraph bGraph, TraverseHelper traverseManager) {
        Set<Vertex> anchorVertices = cache.getAllEntries();
        assert anchorVertices.contains(current);
        Vertex vertex = bGraph.getNeighbours(anchorVertices).stream()
                .min(Comparator.comparing(traverseManager::statusOfVertex)
                        .thenComparing(neighbour -> degreeExcludingDone(bGraph, traverseManager, neighbour))
                        .thenComparing(neighbour -> -neighbour.getWeight())
                ).orElse(null);
        return vertex;
    }

    private Integer degreeExcludingDone(BiGraph bGraph, TraverseHelper traverseManager, Vertex neighbour) {
        return bGraph.degree(neighbour) -
                bGraph.getNeighboursThat(neighbour, nn -> traverseManager.statusOfVertex(nn) == DONE).size();
    }

}
