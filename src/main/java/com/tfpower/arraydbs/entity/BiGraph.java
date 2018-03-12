package com.tfpower.arraydbs.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        return getEdgeBetween(vertex1, vertex2).isPresent();
    }

    public GenericGraph asGenericGraph() {
        return super.copy();
    }
}
