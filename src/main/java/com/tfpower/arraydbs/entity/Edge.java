package com.tfpower.arraydbs.entity;

import com.tfpower.arraydbs.util.Constants;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * Created by vlad on 21.02.18.
 */
public class Edge {

    private String start;
    private String end;
    private Integer weight;
    private String id;
    private String mark;

    public Edge(String id, String start, String end, Integer weight, String mark) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.weight = weight;
        this.mark = mark;
    }

    public Edge(String start, String end, Integer weight, String mark) {
        this(null, start, end, weight, mark);
    }

    public Edge(String start, String end, Integer weight) {
        this(null, start, end, weight, Constants.EMPTY);
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public boolean isIncidentTo(String id) {
        return start.equals(id) || end.equals(id);
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getId() {
        if (id == null || id.equals("")){
            id = computeId();
        }
        return id;
    }

    private String computeId() {
        return String.valueOf(start) + "<-->" + String.valueOf(end) + " " + mark;
    }

    public String endDifferingFrom(String id) {
        return start.equals(id) ? end : start;
    }

    public Set<String> nibs() {
        HashSet<String> nibs = new HashSet<>(2);
        nibs.add(getStart());
        nibs.add(getEnd());
        return nibs;
    }

    @Override
    public String toString() {
        return Stream.of(start, end).sorted().collect(joining("<-->"))
                +
                ((mark != null && !mark.equals(Constants.EMPTY)) ?  "(" + mark + ")" : Constants.EMPTY);
    }


    public Edge copy() {
        return new Edge(this.id, this.start, this.end, this.weight, Constants.EMPTY);
    }


    public static Edge builtBetween(Vertex leftNext, Vertex rightNext) {
        return new Edge(leftNext.getId(), rightNext.getId(), Integer.sum(leftNext.getWeight(), rightNext.getWeight()), Constants.EMPTY);
    }

    public static Edge builtBetween(Vertex leftNext, Vertex rightNext, String mark) {
        return new Edge(leftNext.getId(), rightNext.getId(), Integer.sum(leftNext.getWeight(), rightNext.getWeight()), mark);
    }

}
