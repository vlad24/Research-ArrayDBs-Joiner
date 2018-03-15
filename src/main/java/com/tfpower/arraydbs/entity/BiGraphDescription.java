package com.tfpower.arraydbs.entity;

import java.util.Arrays;
import java.util.List;

public class BiGraphDescription extends GraphDescription implements CSVExportable {
    private int leftVerticesAmount;
    private int rightVerticesAmount;
    private double leftAvgDegree;
    private double rightAvgDegree;
    private double leftMaxDegree;
    private double rightMaxDegree;


    public BiGraphDescription(String graphName) {
        super(graphName);
    }


    public BiGraphDescription(GraphDescription graphDescription) {
        super(graphDescription.getGraphName());
        setAvgDegree(graphDescription.getAvgDegree());
        setMaxDegree(graphDescription.getMaxDegree());
        setMinDegree(graphDescription.getMinDegree());
        setVerticesAmount(graphDescription.getVerticesAmount());
        setEdgesAmount(graphDescription.getEdgesAmount());
    }


    public int getLeftVerticesAmount() {
        return leftVerticesAmount;
    }


    public void setLeftVerticesAmount(int leftVerticesAmount) {
        this.leftVerticesAmount = leftVerticesAmount;
    }


    public int getRightVerticesAmount() {
        return rightVerticesAmount;
    }


    public void setRightVerticesAmount(int rightVerticesAmount) {
        this.rightVerticesAmount = rightVerticesAmount;
    }


    public double getLeftAvgDegree() {
        return leftAvgDegree;
    }


    public void setLeftAvgDegree(double leftAvgDegree) {
        this.leftAvgDegree = leftAvgDegree;
    }


    public double getRightAvgDegree() {
        return rightAvgDegree;
    }


    public void setRightAvgDegree(double rightAvgDegree) {
        this.rightAvgDegree = rightAvgDegree;
    }


    public double getLeftMaxDegree() {
        return leftMaxDegree;
    }


    public void setLeftMaxDegree(double leftMaxDegree) {
        this.leftMaxDegree = leftMaxDegree;
    }


    public double getRightMaxDegree() {
        return rightMaxDegree;
    }


    public void setRightMaxDegree(double rightMaxDegree) {
        this.rightMaxDegree = rightMaxDegree;
    }


    @Override
    public List<String> csvHeaderElements() {
        List<String> header = super.csvHeaderElements();
        header.addAll(Arrays.asList(
                "leftVerticesAmount",
                "rightVerticesAmount",
                "leftAvgDegree",
                "rightAvgDegree",
                "leftMaxDegree",
                "rightMaxDegree"
        ));
        return header;
    }


    @Override
    public List<String> csvElements() {
        List<String> elements = super.csvElements();
        elements.addAll(Arrays.asList(
                String.valueOf(leftVerticesAmount),
                String.valueOf(rightVerticesAmount),
                String.valueOf(leftAvgDegree),
                String.valueOf(rightAvgDegree),
                String.valueOf(leftMaxDegree),
                String.valueOf(rightMaxDegree)
        ));
        return elements;
    }


    @Override
    public String toString() {
        return '{' +
                    super.toString() + "," +
                    "BiGraphDescription{" +
                        "leftVerticesAmount=" + leftVerticesAmount +
                        ", rightVerticesAmount=" + rightVerticesAmount +
                        ", leftAvgDegree=" + leftAvgDegree +
                        ", rightAvgDegree=" + rightAvgDegree +
                        ", leftMaxDegree=" + leftMaxDegree +
                        ", rightMaxDegree=" + rightMaxDegree +
                    '}'
                +"}"
                ;
    }
}
