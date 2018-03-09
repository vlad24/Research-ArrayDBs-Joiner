package com.tfpower.arraydbs.entity;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiFunction;

import static java.util.Collections.emptySet;

@Component
public class TraverseHelper {


    public enum Status {
        UNTOUCHED,
        IN_PROGRESS,
        DONE
    }

    private boolean finished;
    private Deque<Vertex> visitResult;
    private Deque<Vertex> visitBuffer;
    private Map<String, Status> vertexStatus;
    private Map<String, Status> edgeStatus;
    private Map<Status, Set<String>> statusesInfo;
    private Map<String, Integer> vertexVisitCount;
    private BiFunction<Integer, Vertex, Integer> accumulatorUpdater;
    private Integer accumulator;

    public TraverseHelper() {
        this.finished = false;
        this.visitResult = new LinkedList<>();
        this.visitBuffer = new LinkedList<>();
        this.vertexVisitCount = new HashMap<>();
        this.vertexStatus = new HashMap<>();
        this.edgeStatus = new HashMap<>();
        this.statusesInfo = new HashMap<>(Status.values().length);
        this.accumulator = 0;
        accumulatorUpdater = (acc, v) -> acc;
    }


    public void pushToVisitBuffer(Vertex vertex) {
        visitBuffer.addLast(vertex);
    }

    public Vertex popFromVisitBuffer() {
        return visitBuffer.removeLast();
    }

    public void pushToVisitResult(Vertex vertex) {
        visitResult.addLast(vertex);
    }

    public Vertex popFromVisitResult() {
        return visitResult.removeLast();
    }

    public void accountVisit(Vertex vertex) {
        vertexVisitCount.merge(vertex.getId(), 1, Integer::sum);
    }

    public void markVertex(Vertex vertex, Status status) {
        markVertex(vertex.getId(), status);
    }

    public void markVertex(String vertexId, Status newStatus) {
        Status oldStatus = vertexStatus.get(vertexId);
        if (oldStatus != newStatus) {
            statusesInfo.getOrDefault(oldStatus, emptySet()).remove(vertexId);
            statusesInfo.computeIfAbsent(newStatus, status -> new HashSet<>());
            statusesInfo.compute(newStatus, (status, vertices) -> {
                vertices.add(vertexId);
                return vertices;
            });
            vertexStatus.put(vertexId, newStatus);
        }
    }

    public void markEdge(String edgeId, Status progress) {
        edgeStatus.put(edgeId, progress);
    }

    public void markEdge(Edge edge, Status progress) {
        markEdge(edge.getId(), progress);
    }

    public Status statusOfVertex(Vertex v) {
        return vertexStatus.getOrDefault(v.getId(), Status.UNTOUCHED);
    }

    public Status statusOfEdge(Edge edge) {
        return vertexStatus.getOrDefault(edge.getId(), Status.UNTOUCHED);
    }

    public Deque<Vertex> getVisitResult() {
        return visitResult;
    }

    public Deque<Vertex> getVisitBuffer() {
        return visitBuffer;
    }

    public boolean isNotFinished() {
        return !finished;
    }

    public void finish() {
        finished = true;
    }

    public void finishIf(boolean condition) {
        finished = condition;
    }

    public int countEdgesMarked(Status status) {
        return ((Long) (edgeStatus.entrySet().stream().filter(e -> status.equals(e.getValue())).count())).intValue();
    }

    public int countVerticesMarked(Status status) {
        return statusesInfo.getOrDefault(status, emptySet()).size();
    }

    public void markVertices(Collection<String> vertexIds, Status status) {
        vertexIds.forEach(vertex -> markVertex(vertex, status));
    }

    public Map<String, Integer> getVisitCountsPerVertices() {
        return vertexVisitCount;
    }

    public void setAccumulatorUpdater(BiFunction<Integer, Vertex, Integer> accumulatorUpdater) {
        this.accumulatorUpdater = accumulatorUpdater;
    }

    public void updateAccumulatorBy(Vertex vertex) {
        accumulator = accumulatorUpdater.apply(accumulator, vertex);
    }

    public Integer getAccumulator() {
        return accumulator;
    }

    public Map<String, Status> getEdgeStatus() {
        return edgeStatus;
    }

    public Map<String, Status> getVertexStatus() {
        return vertexStatus;
    }

    @Override
    public String toString() {
        return "TraverseManager{" +
                "  accumulator=" + accumulator + "\n" +
                "  vertexStatus=" + vertexStatus + "\n" +
                "  vertexVisitCount=" + vertexVisitCount + "\n" +
                "  edgeStatus=" + edgeStatus + "\n" +
                "  statusesInfo=" + statusesInfo + "\n" +
                "  accumulatorUpdater=" + accumulatorUpdater + "\n" +
                "  visitResult=" + visitResult + "\n" +
                '}';
    }


}
