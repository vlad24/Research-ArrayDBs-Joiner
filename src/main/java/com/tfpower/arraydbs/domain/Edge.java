package com.tfpower.arraydbs.domain;

/**
 * Created by vlad on 21.02.18.
 */
public class Edge {

    private Integer start;
    private Integer end;
    private Integer weight;

    public Edge(Integer start, Integer end, Integer weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public boolean isIncidentTo(Integer id) {
        return start.equals(id) || end.equals(id);
    }

    public Integer endDifferingFrom(Integer id) {
        return start.equals(id) ? end : start;
    }
}
