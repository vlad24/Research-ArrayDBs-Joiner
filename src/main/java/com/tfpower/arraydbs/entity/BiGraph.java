package com.tfpower.arraydbs.entity;

import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * Created by vlad on 21.02.18.
 */
public class BiGraph extends GenericGraph {

    private Set<String> leftVerticesIds;
    private Set<String> rightVerticesIds;

    public BiGraph(){
        super();
        rightVerticesIds = new HashSet<>();
        leftVerticesIds = new HashSet<>();
    }

    public BiGraph(String name){
        super(name);
        rightVerticesIds = new HashSet<>();
        leftVerticesIds = new HashSet<>();
    }

    public void addLeftVertex(Vertex vertex) {
        super.addVertex(vertex);
        leftVerticesIds.add(vertex.getId());
        vertexIndex.put(vertex.getId(), vertex);
    }

    public void addRightVertex(Vertex vertex) {
        super.addVertex(vertex);
        rightVerticesIds.add(vertex.getId());
        vertexIndex.put(vertex.getId(), vertex);
    }

    @Override
    public void addEdge(Edge edge) {
        if (super.isEdgeAllowed(edge) && doesNotBreakBiProperty(edge)){
            super.addEdge(edge);
        }
    }

    private boolean doesNotBreakBiProperty(Edge edge) {
        return (leftVerticesIds.contains(edge.getStart()) && rightVerticesIds.contains(edge.getEnd()))
                || (rightVerticesIds.contains(edge.getStart()) && leftVerticesIds.contains(edge.getEnd()));
    }


    public Set<Vertex> getLeftVertices() {
        return leftVerticesIds.stream().map(this::getExistingVertex).collect(Collectors.toSet());
    }

    public Set<Vertex> getRightVertices() {
        return rightVerticesIds.stream().map(this::getExistingVertex).collect(Collectors.toSet());
    }

    @Override
    public BiGraph copy() {
        BiGraph cloneGraph = new BiGraph();
        getLeftVertices().stream().map(Vertex::copy).forEach(cloneGraph::addLeftVertex);
        getRightVertices().stream().map(Vertex::copy).forEach(cloneGraph::addRightVertex);
        edges.stream().map(Edge::copy).forEach(cloneGraph::addEdge);
        return cloneGraph;
    }

    public Boolean areAsided(Vertex vertex1, Vertex vertex2) {
        return leftVerticesIds.contains(vertex1.getId()) && rightVerticesIds.contains(vertex2.getId()) ||
                leftVerticesIds.contains(vertex2.getId()) && rightVerticesIds.contains(vertex1.getId());
    }

    public boolean areDirectlyConnected(Vertex vertex1, Vertex vertex2) {
        return getSingleEdgeBetween(vertex1, vertex2).isPresent();
    }

    public GenericGraph asGenericGraph() {
        return super.copy();
    }


    @Override
    public GraphDescription description() {
        BiGraphDescription biGraphDescription = new BiGraphDescription(super.description());
        biGraphDescription.setLeftVerticesAmount(getLeftVertices().size());
        biGraphDescription.setRightVerticesAmount(getRightVertices().size());
        IntSummaryStatistics leftDegStats = getLeftVertices().stream().mapToInt(this::degree).summaryStatistics();
        IntSummaryStatistics rightDegStats = getRightVertices().stream().mapToInt(this::degree).summaryStatistics();
        biGraphDescription.setLeftAvgDegree(leftDegStats.getAverage());
        biGraphDescription.setRightAvgDegree(rightDegStats.getAverage());
        biGraphDescription.setLeftMaxDegree(leftDegStats.getMax());
        biGraphDescription.setRightMaxDegree(rightDegStats.getMax());
        return biGraphDescription;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("----------------------").append(getName()).append("-------------\n")
                .append(leftVerticesIds.stream().map(lId -> getExistingVertex(lId).toString()).collect(joining(" "))).append("\n")
                .append(rightVerticesIds.stream().map(rId -> getExistingVertex(rId).toString()).collect(joining(" "))).append("\n")
                .append(leftVerticesIds.stream().map(leftId ->
                            leftId + "->" + getNeighbours(leftId).stream().map(Vertex::getId).sorted().collect(joining("\t")))
                        .collect(joining("\n"))
                )
                .append("\n----------------------------------------------------------------")
                .toString();
    }
}
