package com.tfpower.arraydbs.entity;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by vlad on 24.01.18.
 */
public class JoinReport {
    private List<String> traverseSequence;
    private Map<String, Integer> loadFrequencies;
    private Integer totalWeight;

    public List<String> getTraverseSequence() {
        return traverseSequence;
    }

    public void setTraverseSequence(List<String> traverseSequence) {
        this.traverseSequence = traverseSequence;
    }

    public Map<String, Integer> getLoadFrequencies() {
        return loadFrequencies;
    }

    public void setLoadFrequencies(Map<String, Integer> loadFrequencies) {
        this.loadFrequencies = loadFrequencies;
    }

    public Integer setTotalCost() {
        return totalWeight;
    }

    public void setTotalCost(Integer totalWeight) {
        this.totalWeight = totalWeight;
    }

    @Override
    public String toString() {
        return "JoinReport{\n\t" +
                    "'traverseSequence':" + traverseSequence + ",\n\t" +
                    "'loadFrequencies':" + loadFrequencies + ",\n\t" +
                    "'totalWeight':" + totalWeight + "\n" +
                '}';
    }


    public static JoinReport fromTraversal(TraverseHelper traverseHelper){
        JoinReport joinReport = new JoinReport();
        joinReport.setTotalCost(traverseHelper.getAccumulator());
        joinReport.setLoadFrequencies(new TreeMap<>(traverseHelper.getVisitCountsPerVertices()));
        joinReport.setTraverseSequence(traverseHelper.getVisitResult().stream()
                .map(Vertex::getId)
                .collect(Collectors.toList())
        );
        return joinReport;
    }
}
