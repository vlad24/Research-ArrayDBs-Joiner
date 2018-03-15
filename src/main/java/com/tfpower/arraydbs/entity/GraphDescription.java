package com.tfpower.arraydbs.entity;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class GraphDescription implements CSVExportable{
    private String graphName;
    private int edgesAmount;
    private int verticesAmount;
    private double avgDegree;
    private double maxDegree;
    private double minDegree;


    public GraphDescription(String graphName) {
        this.graphName = graphName;
    }

    public String getGraphName() {
        return graphName;
    }


    public int getEdgesAmount() {
        return edgesAmount;
    }


    public void setEdgesAmount(int edgesAmount) {
        this.edgesAmount = edgesAmount;
    }


    public int getVerticesAmount() {
        return verticesAmount;
    }


    public void setVerticesAmount(int verticesAmount) {
        this.verticesAmount = verticesAmount;
    }


    public double getAvgDegree() {
        return avgDegree;
    }


    public void setAvgDegree(double avgDegree) {
        this.avgDegree = avgDegree;
    }


    public double getMaxDegree() {
        return maxDegree;
    }


    public void setMaxDegree(double maxDegree) {
        this.maxDegree = maxDegree;
    }


    public double getMinDegree() {
        return minDegree;
    }


    public void setMinDegree(double minDegree) {
        this.minDegree = minDegree;
    }


    @Override
    public List<String> csvHeaderElements() {
        return Stream.of(
                "graphName",
                "edgesAmount",
                "verticesAmount",
                "avgDegree",
                "maxDegree",
                "minDegree"
        ).collect(toList());
    }


    @Override
        public List<String> csvElements() {
            return Stream.of(
                graphName,
                String.valueOf(edgesAmount),
                String.valueOf(verticesAmount),
                String.valueOf(avgDegree),
                String.valueOf(maxDegree),
                String.valueOf(minDegree)
        ).collect(toList());
    }


    @Override
    public String toString() {
        return "GraphDescription{" +
                "graphName='" + graphName + '\'' +
                ", edgesAmount=" + edgesAmount +
                ", verticesAmount=" + verticesAmount +
                ", avgDegree=" + avgDegree +
                ", maxDegree=" + maxDegree +
                ", minDegree=" + minDegree +
                '}';
    }
}
