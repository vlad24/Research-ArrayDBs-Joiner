package com.tfpower.arraydbs.domain;

import java.util.List;
import java.util.Map;

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
        return "JoinReport{\n" +
                    "traverseSequence=" + traverseSequence + ",\n" +
                    "loadFrequencies=" + loadFrequencies + ",\n" +
                    "totalWeight=" + totalWeight + "\n" +
                '}';
    }
}
