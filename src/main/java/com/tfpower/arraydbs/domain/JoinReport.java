package com.tfpower.arraydbs.domain;

import java.util.List;
import java.util.Map;

/**
 * Created by vlad on 24.01.18.
 */
public class JoinReport {
    private List<Integer> traverseSequence;
    private Map<Integer, Integer> loadFrequencies;
    private Integer totalWeight;

    public List<Integer> getTraverseSequence() {
        return traverseSequence;
    }

    public void setTraverseSequence(List<Integer> traverseSequence) {
        this.traverseSequence = traverseSequence;
    }

    public Map<Integer, Integer> getLoadFrequencies() {
        return loadFrequencies;
    }

    public void setLoadFrequencies(Map<Integer, Integer> loadFrequencies) {
        this.loadFrequencies = loadFrequencies;
    }

    public Integer getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Integer totalWeight) {
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
