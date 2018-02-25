package com.tfpower.arraydbs.domain;

import static com.tfpower.arraydbs.util.Constants.ABSENT;

/**
 * Created by vlad on 21.02.18.
 */
public class Vertex {
    private Integer id;
    private String name;
    private Integer weight;

    public Vertex(Integer id) {
        this.id = id;
        this.name = "";
        this.weight = ABSENT;
    }

    public Vertex(Integer id, Integer weight) {
        this.id = id;
        this.name = "";
        this.weight = weight;
    }

    public Vertex(Integer id, String name, Integer weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
    }

    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
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
        return "V[" + id +"]_" + weight + (name != null && name.isEmpty() ? "//" + name : "");
    }
}
