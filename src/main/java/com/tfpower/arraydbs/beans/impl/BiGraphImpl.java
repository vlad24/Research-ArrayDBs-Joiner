package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.BiGraph;
import com.tfpower.arraydbs.domain.Edge;
import com.tfpower.arraydbs.domain.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static com.tfpower.arraydbs.beans.impl.BiGraphImpl.TraversalProgress.Status.DONE;
import static com.tfpower.arraydbs.beans.impl.BiGraphImpl.TraversalProgress.Status.IN_PROGRESS;
import static com.tfpower.arraydbs.beans.impl.BiGraphImpl.TraversalProgress.Status.UNTOUCHED;
import static java.util.stream.Collectors.toSet;

/**
 * Created by vlad on 21.02.18.
 */
@Component
public class BiGraphImpl implements BiGraph {

    @Autowired
    BiGraphFileIncListParser parser;

    private Set<Integer> leftVertices;
    private Set<Integer> rightVertices;
    private Set<Edge> edges;
    private Map<Integer, Vertex> vertexIndex;
    private Map<Integer, Set<Edge>> incidenceMap;

    public BiGraphImpl(){
        vertexIndex = new HashMap<>();
        incidenceMap = new HashMap<>();
        rightVertices = new HashSet<>();
        leftVertices = new HashSet<>();
        edges = new HashSet<>();
        vertexIndex = new HashMap<>();
    }

    @PostConstruct
    private void initGraphFromFile() throws IOException, ParseException {
        parser.buildFromFile(this);
    }

    @Override
    public void addFirstClassVertex(Vertex vertex) {
        leftVertices.add(vertex.getId());
        vertexIndex.put(vertex.getId(), vertex);
    }

    @Override
    public void addSecondClassVertex(Vertex vertex) {
        rightVertices.add(vertex.getId());
        vertexIndex.put(vertex.getId(), vertex);
    }

    @Override
    public void addEdge(Edge edge) {
        incidenceMap.putIfAbsent(edge.getStart(), new HashSet<>());
        incidenceMap.putIfAbsent(edge.getEnd(), new HashSet<>());
        incidenceMap.get(edge.getStart()).add(edge);
        incidenceMap.get(edge.getEnd()).add(edge);
    }

    @Override
    public Set<Vertex> getLeftVertices() {
        return vertexIndex.entrySet().stream()
                .filter(e -> leftVertices.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .collect(toSet());
    }

    @Override
    public Set<Vertex> getRightVertices() {
        return vertexIndex.entrySet().stream()
                .filter(e -> rightVertices.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .collect(toSet());
    }

    @Override
    public Set<Edge> getEdges() {
        return edges;
    }

    @Override
    public Set<Vertex> getNeighboursOf(Integer vertexId) {
        return incidenceMap.getOrDefault(vertexId, Collections.emptySet()).stream()
                .map(e -> getVertexByIdOrFail(e.endDifferingFrom(vertexId)))
                .collect(toSet());
    }

    @Override
    public Optional<Vertex> getVertexById(Integer id) {
        return Optional.ofNullable(vertexIndex.get(id));
    }

    @Override
    public Optional<Edge> getEdgeBetween(Integer leftId, Integer rightId) {
        return this.incidenceMap.get(leftId).stream().filter(edge -> edge.isIncidentTo(rightId)).findAny();
    }

    @Override
    public Map<Integer, Set<Edge>> getIncidenceMap() {
        return incidenceMap;
    }

    @Override
    public Optional<Queue<Vertex>> getPathBetween(Integer firstId, Integer secondId) {
        return searchPathBetween(secondId, firstId, new TraversalProgress());
    }

    private Optional<Queue<Vertex>> searchPathBetween(Integer destinationVertexId, Integer current, TraversalProgress traversal) {
        if (current.equals(destinationVertexId)) {
            traversal.finish();
            return Optional.of(traversal.getPath());
        } else {
            traversal.appendToPath(getVertexByIdOrFail(current));
            traversal.markVertex(current, IN_PROGRESS);
            Set<Vertex> unvisitedNeighbours = getNeighboursOf(current)
                    .stream().filter(n -> traversal.statusOfVertex(n) == UNTOUCHED).collect(toSet());
            if (unvisitedNeighbours.isEmpty()) {
                traversal.markVertex(current, DONE);
            } else {
                Iterator<Vertex> iterator = unvisitedNeighbours.iterator();
                while (iterator.hasNext() && !traversal.isFinished()) {
                    Integer neighbourId = iterator.next().getId();
                    searchPathBetween(destinationVertexId, neighbourId, traversal);
                    if (!traversal.isFinished()) {
                        traversal.popFromPath();
                    }
                }
            }
            return Optional.empty();
        }
    }

    static class TraversalProgress {
        boolean finished;
        Deque<Vertex> path;
        Map<Integer, Status> vertexStatus;
        Map<Integer, Status> edgeStatus;
        Integer accumulator;

        public TraversalProgress() {
            this.finished = false;
            this.path = new LinkedList<>();
            this.vertexStatus = new HashMap<>();
            this.edgeStatus = new HashMap<>();
            this.accumulator = 0;
        }

        public void appendToPath(Vertex vertex) {
            path.addLast(vertex);
        }

        public void popFromPath(){
            path.removeLast();
        }

        public void markVertex(Integer vertexId, Status progress) {
            vertexStatus.put(vertexId, progress);
        }

        public void markEdge(Integer edgeId, Status progress) {
            vertexStatus.put(edgeId, progress);
        }

        public Status statusOfVertex(Vertex v) {
            return vertexStatus.getOrDefault(v, UNTOUCHED);
        }

        public Deque<Vertex> getPath() {
            return path;
        }

        public boolean isFinished() {
            return finished;
        }

        public void finish(){
            finished = true;
        }

        enum Status {
            UNTOUCHED,
            IN_PROGRESS,
            DONE
        }
    }
}
