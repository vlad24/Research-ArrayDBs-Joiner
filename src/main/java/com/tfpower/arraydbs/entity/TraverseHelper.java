package com.tfpower.arraydbs.entity;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiFunction;

import static java.util.Collections.emptySet;

@Component
public class TraverseHelper {


    public enum Status {
        UNTOUCHED,
        IN_PROGRESS,
        DONE;
    }

    private boolean finished;
    private Deque<Vertex> visitResult;
    private Deque<Vertex> visitBuffer;
    private Map<String, Status> vertexStatus;
    private Map<String, Status> edgeStatus;
    private Map<Status, Set<String>> statusVertices;
    private Map<String, Integer> vertexVisitCount;
    private Map<String, Integer> edgeVisitCount;
    private BiFunction<Integer, Vertex, Integer> accumulatorUpdater;
    private Integer accumulator;


    public TraverseHelper() {
        this.finished = false;
        this.visitResult = new LinkedList<>();
        this.visitBuffer = new LinkedList<>();
        this.vertexVisitCount = new HashMap<>();
        this.edgeVisitCount = new HashMap<>();
        this.vertexStatus = new HashMap<>();
        this.edgeStatus = new HashMap<>();
        this.statusVertices = new HashMap<>(Status.values().length);
        this.accumulator = 0;
        accumulatorUpdater = (acc, v) -> acc;
    }

    public void pushToVisitBuffer(Vertex vertex) {
        failIfFinished();
        visitBuffer.addLast(vertex);
    }

    public Vertex popFromVisitBuffer() {
        failIfFinished();
        return visitBuffer.removeLast();
    }

    public void pushToVisitResult(Vertex vertex) {
        failIfFinished();
        visitResult.addLast(vertex);
    }

    public Vertex popFromVisitResult() {
        failIfFinished();
        return visitResult.removeLast();
    }

    public void accountVertexVisit(Vertex vertex) {
        failIfFinished();
        vertexVisitCount.merge(vertex.getId(), 1, Integer::sum);
    }

    public void accountEdgeVisit(Edge edge) {
        failIfFinished();
        edgeVisitCount.merge(edge.getId(), 1, Integer::sum);
    }

    public void markVertex(Vertex vertex, Status status) {
        failIfFinished();
        markVertex(vertex.getId(), status);
    }

    public void markVertex(String vertexId, Status newStatus) {
        failIfFinished();
        Status oldStatus = vertexStatus.get(vertexId);
        if (oldStatus != newStatus) {
            statusVertices.getOrDefault(oldStatus, emptySet()).remove(vertexId);
            statusVertices.computeIfAbsent(newStatus, status -> new HashSet<>());
            statusVertices.compute(newStatus, (status, vertices) -> {
                vertices.add(vertexId);
                return vertices;
            });
            vertexStatus.put(vertexId, newStatus);
        }
    }

    public void markEdge(String edgeId, Status progress) {
        failIfFinished();
        edgeStatus.put(edgeId, progress);
    }

    public void markEdge(Edge edge, Status progress) {
        failIfFinished();
        markEdge(edge.getId(), progress);
    }

    public void markEdges(Set<Edge> edges, Status progress) {
        edges.forEach(edge -> markEdge(edge, progress));
    }

    public Status statusOfVertex(Vertex v) {
        return vertexStatus.getOrDefault(v.getId(), Status.UNTOUCHED);
    }

    public Status statusOfEdge(Edge edge) {
        return edgeStatus.getOrDefault(edge.getId(), Status.UNTOUCHED);
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
        return statusVertices.getOrDefault(status, emptySet()).size();
    }

    public void markVertices(Collection<String> vertexIds, Status status) {
        vertexIds.forEach(vertex -> markVertex(vertex, status));
    }

    public Map<String, Integer> getVertexVisitCount() {
        return vertexVisitCount;
    }

    public Map<String, Integer> getEdgeVisitCount() {
        return edgeVisitCount;
    }

    public void setAccumulatorUpdater(BiFunction<Integer, Vertex, Integer> accumulatorUpdater) {
        failIfFinished();
        this.accumulatorUpdater = accumulatorUpdater;
    }

    public void updateAccumulatorBy(Vertex vertex) {
        failIfFinished();
        accumulator = accumulatorUpdater.apply(accumulator, vertex);
    }

    private void failIfFinished() {
        if (finished){
            throw new IllegalStateException("Traverse is already finished : could not modify results. Traverse: " + toString());
        }
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
                "  statusVertices=" + statusVertices + "\n" +
                "  accumulatorUpdater=" + accumulatorUpdater + "\n" +
                "  visitResult=" + visitResult + "\n" +
                '}';
    }


}
