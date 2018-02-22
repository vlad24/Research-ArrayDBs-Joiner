package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.BiGraph;
import com.tfpower.arraydbs.beans.BiGraphFileIncListParser;
import com.tfpower.arraydbs.domain.Edge;
import com.tfpower.arraydbs.domain.Vertex;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by vlad on 21.02.18.
 */
public class BiGraphImpl implements BiGraph {

    @Autowired
    BiGraphFileIncListParser parser;

    private List<List<Integer>> biMatrix;
    private String firstClassPrefix;
    private String secondClassPrefix;

    public BGraph(){
    }

    @PostConstruct
    private void initGraphFromFile() throws IOException {
        parser.buildFromFile(this);
    }

    @Override
    public Set<Vertex> getLeftVertices() {
        return null;
    }

    @Override
    public Set<Vertex> getRightVertices() {
        return null;
    }

    @Override
    public List<Edge> getEdges() {
        return null;
    }

    @Override
    public Optional<Vertex> getVertexById(Integer id) {
        return null;
    }

    @Override
    public Optional<Edge> getEdgeBetween(Integer leftId, Integer rightId) {
        return null;
    }

    @Override
    public Map<Integer, List<Vertex>> getNeighbourMap() {
        return null;
    }

    @Override
    public Optional<List<Vertex>> getPathBetween(Vertex first, Vertex second) {
        return null;
    }

    @Override
    public Optional<List<BiGraph>> getConnectedComponents() {
        return null;
    }
}
