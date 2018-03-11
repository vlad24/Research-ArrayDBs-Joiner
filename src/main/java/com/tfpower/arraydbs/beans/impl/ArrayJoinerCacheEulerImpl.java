package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.Cache;
import com.tfpower.arraydbs.entity.*;
import com.tfpower.arraydbs.util.Constants;
import com.tfpower.arraydbs.util.Randomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.tfpower.arraydbs.entity.TraverseHelper.Status.DONE;
import static java.util.stream.Collectors.toSet;

/**
 * Created by vlad on 24.01.18.
 */
@Component
public class ArrayJoinerCacheEulerImpl implements ArrayJoiner {

    private final static Logger logger = LoggerFactory.getLogger(ArrayJoinerCacheEulerImpl.class);
    @Autowired
    Cache<Vertex> cache;

    public JoinReport join(BiGraph bGraph) {
        GenericGraph graph = augment(bGraph);
        TraverseHelper traverseHelper = new TraverseHelper();
        buildCacheAwareEulerCycle(graph, traverseHelper);
        return JoinReport.fromTraversal(traverseHelper);
    }


    private GenericGraph augment(BiGraph biGraph) {
        GenericGraph resultGraph = biGraph.asGenericGraph();
        Set<Vertex> oddVertices = Stream.concat(biGraph.getLeftVertices().stream(), biGraph.getRightVertices().stream())
                .filter(v -> biGraph.degree(v) % 2 != 0).collect(toSet());
        Set<Vertex> connected = connectIf((v1, v2) -> biGraph.areAsided(v1, v2) && biGraph.areDirectlyConnected(v1, v2),
                resultGraph, oddVertices);
        oddVertices.removeAll(connected);
        connected = connectIf(biGraph::areDirectlyConnected, resultGraph, oddVertices);
        oddVertices.removeAll(connected);
        connected = connectIf((v1, v2) -> true, resultGraph, oddVertices);
        oddVertices.removeAll(connected);
        assert oddVertices.isEmpty();
        return resultGraph;
    }


    private Set<Vertex> connectIf(BiFunction<Vertex, Vertex, Boolean> predicate, GenericGraph graph, Set<Vertex> vertexSet) {
        Set<Vertex> justConnectedVertices = new HashSet<>();
        for (Vertex firstVertex : vertexSet) {
            for (Vertex secondVertex : vertexSet) {
                if (!firstVertex.equals(secondVertex) &&
                        !justConnectedVertices.contains(firstVertex) &&
                        !justConnectedVertices.contains(secondVertex) &&
                        predicate.apply(firstVertex, secondVertex)) {
                    justConnectedVertices.add(firstVertex);
                    justConnectedVertices.add(secondVertex);
                    graph.addEdge(Edge.builtBetween(firstVertex, secondVertex, Constants.AUX));
                }
            }
        }
        return justConnectedVertices;
    }


    private void buildCacheAwareEulerCycle(GenericGraph graph, TraverseHelper traverse) {
        boolean graphIsValid = graph.getAllVertices().stream().allMatch(v -> graph.degree(v) % 2 == 0);
        if (!graphIsValid) {
            throw new IllegalArgumentException("Invalid graph passed to euler cycle path search method :" + graph);
        }
        logger.debug(graph + "");
        Vertex current = Randomizer.pickRandomFrom(graph.getAllVertices());
        traverse.pushToVisitBuffer(current);
        traverse.setAccumulatorUpdater((acc, v) -> acc + v.getWeight());
        while (traverse.isNotFinished()) {
            final Vertex examinedVertex = current;
            Set<Vertex> reachableNeighbours = graph.getNeighboursThat(
                    neighbor -> graph.getAllEdgesBetween(neighbor, examinedVertex).stream()
                            .anyMatch(neighborEdge -> traverse.statusOfEdge(neighborEdge) != DONE),
                    current
            );
            if (reachableNeighbours.isEmpty()) {
                logger.debug("Current is {}", current);
                traverse.pushToVisitResult(current);     // push to circuit
                if (!cache.contains(current)) {
                    logger.debug("Trying to add {}", current);
                    if (cache.getCurrentSize() < cache.getCapacity()) {
                        cache.load(current);
                        logger.debug("Loaded to free space!");
                    } else {
                        Vertex evicted = cache.evict(Cache.oldest());
                        logger.debug("Loaded instead of {}", evicted);
                        cache.load(current);
                    }
                    traverse.accountVisit(current);
                    traverse.updateAccumulatorBy(current);
                }
                current = traverse.popFromVisitBuffer(); // reset the 'current' one
            } else {
                traverse.pushToVisitBuffer(current);
                Vertex next = Randomizer.pickRandomFrom(reachableNeighbours);
                Edge edgeToNext = Randomizer.pickRandomFrom(graph.getAllEdgesBetween(current, next));
                traverse.markEdge(edgeToNext, DONE);
                current = next;
            }
            traverse.finishIf(traverse.getVisitBuffer().isEmpty() && reachableNeighbours.isEmpty());
        }
        assert graph.getAllEdges().stream().map(traverse::statusOfEdge).allMatch(status -> status.equals(DONE));
    }
}
