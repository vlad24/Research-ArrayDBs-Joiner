package com.tfpower.arraydbs.domain;

import java.util.Objects;

import static com.tfpower.arraydbs.util.Constants.ABSENT;

/**
 * Created by vlad on 21.02.18.
 */
public class Vertex {
    private String id;
    private String name;
    private Integer weight;

    public Vertex(String id) {
        this.id = id;
        this.name = "";
        this.weight = ABSENT;
    }

    public Vertex(String id, Integer weight) {
        this.id = id;
        this.name = "";
        this.weight = weight;
    }

    public Vertex(String id, String name, Integer weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "#" + id +"_" + weight + (name != null && !name.isEmpty() ? "//" + name : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Objects.equals(id, vertex.id) &&
                Objects.equals(name, vertex.name) &&
                Objects.equals(weight, vertex.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, weight);
    }
}
