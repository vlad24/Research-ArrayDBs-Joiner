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

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.tfpower.arraydbs.entity.TraverseHelper.Status.DONE;
import static java.util.stream.Collectors.toList;
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
        logger.info("{} is joining {}", this.toString(), bGraph.getName());
        GenericGraph graph = augment(bGraph);
        TraverseHelper cycleTraverse = buildEulerCycleUpdatingCache(graph);
        TraverseHelper cacheCycleTraverse  = calculateCosts(graph, cycleTraverse);
        return JoinReport.fromGraphTraversal(cacheCycleTraverse, this.toString(), bGraph.description());
    }

    private TraverseHelper calculateCosts(GenericGraph graph, TraverseHelper cycleTraverse) {
        cache.clear();
        TraverseHelper pathTraverse = new TraverseHelper();
        pathTraverse.setAccumulatorUpdater((acc, v) -> acc + v.getWeight());
        Deque<Vertex> visitSeq = cycleTraverse.getVisitResult();
        Iterator<Vertex> vertexIterator = visitSeq.iterator();
        Vertex current = vertexIterator.next();
        Vertex prev = current;
        cache.loadOrFail(current);
        pathTraverse.accountVertexVisit(current);
        while (vertexIterator.hasNext() && pathTraverse.isNotFinished()){
            prev = current;
            current = vertexIterator.next();
            if (!cache.contains(current)) {
                cache.loadOrEvict(current, Cache.anyExceptFor(prev).thenComparing(Cache.byAge()));
                pathTraverse.updateAccumulatorBy(current);
                pathTraverse.accountVertexVisit(current);
            }
            Set<Edge> edgesInCache = graph.getEdgesAround(current, cache.getAllValues());
            pathTraverse.markEdges(edgesInCache, DONE);
            List<Edge> notProcessed = graph.getAllEdges().stream().filter(e -> pathTraverse.statusOfEdge(e) != DONE).collect(toList());
            pathTraverse.finishIf(notProcessed.size() == 0);
        }
        assert graph.getAllEdges().stream().map(pathTraverse::statusOfEdge).allMatch(status -> status.equals(DONE)):
                "Not all edges are eventually marked done";
        return pathTraverse;
    }


    private TraverseHelper buildEulerCycleUpdatingCache(GenericGraph graph) {
        boolean graphIsValid = graph.getAllVertices().stream().allMatch(v -> graph.degree(v) % 2 == 0);
        if (!graphIsValid) {
            throw new IllegalArgumentException("Invalid graph passed to euler cycle path search method :" + graph);
        }
        TraverseHelper traverse = new TraverseHelper();
        Vertex current = Randomizer.pickRandomFrom(graph.getAllVertices());
        traverse.pushToVisitBuffer(current);
        traverse.setAccumulatorUpdater((acc, v) -> acc + v.getWeight());
        while (traverse.isNotFinished()) {
            final Vertex examinedVertex = current;
            Set<Vertex> reachableNeighbours = graph.getNeighboursThat(
                    neighbor -> graph.getAllEdgesBetween(examinedVertex, neighbor).stream().anyMatch(neighborEdge -> traverse.statusOfEdge(neighborEdge) != DONE),
                    current
            );
            if (reachableNeighbours.isEmpty()) {
                logger.trace("Current is {}", current);
                traverse.pushToVisitResult(current);     // push to circuit
                current = traverse.popFromVisitBuffer(); // reset the 'current' one
            } else {
                traverse.pushToVisitBuffer(current);
                Vertex next = Randomizer.pickRandomFrom(reachableNeighbours);
                Edge edgeToNext = Randomizer.pickRandomFrom(graph.getAllEdgesBetween(current, next).stream().filter(e -> traverse.statusOfEdge(e) != DONE).collect(toSet()));
                traverse.markEdge(edgeToNext, DONE);
                traverse.accountEdgeVisit(edgeToNext);
                current = next;
            }
            traverse.finishIf(traverse.getVisitBuffer().isEmpty() && reachableNeighbours.isEmpty());
        }
        assert traverse.getVisitResult().getFirst().equals(traverse.getVisitResult().getLast()) : "Not a circuit found";
        assert traverse.getEdgeVisitCount().keySet().containsAll(graph.getAllEdgesIds()) : "Not all edges were traversed";
        assert traverse.getEdgeVisitCount().values().stream().allMatch(f -> f == 1) : "Not all edges are processed only once";
        return traverse;
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



    @Override
    public String toString() {
        return "euler-joiner<" + cache.getCapacity() + ">";
    }
}
