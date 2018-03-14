package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.BiGraphParser;
import com.tfpower.arraydbs.beans.BiGraphProvider;
import com.tfpower.arraydbs.entity.BiGraph;
import com.tfpower.arraydbs.entity.Edge;
import com.tfpower.arraydbs.entity.Vertex;
import com.tfpower.arraydbs.util.Constants;
import com.tfpower.arraydbs.util.Randomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

@Component
public class BGraphProviderByRandomImpl implements BiGraphProvider {

    private final String LEFT_PREFIX = "A";
    private final String RIGHT_PREFIX = "B";

    @Value("${graphs.random.left_set.capacity}")
    private Integer graphsCount;

    @Value("${graphs.random.left_set.capacity}")
    private Integer leftSetCapacity;


    @Value("${graphs.random.right_set.capacity}")
    private Integer rightSetCapacity;

    @Value("${graphs.random.edges}")
    private Integer edgesAmount;


    @Override
    public List<BiGraph> getTestGraphs() {
        List<BiGraph> graphs = new ArrayList<>(graphsCount);
        for (int i = 0; i < graphsCount; i++) {
            BiGraph graph = new BiGraph("Graph " + i);
            IntStream.range(1, 1 + leftSetCapacity)
                    .mapToObj(j -> new Vertex(LEFT_PREFIX + j, Constants.EMPTY, Randomizer.randomPositiveSmallInt()))
                    .forEach(graph::addLeftVertex);
            IntStream.range(1, 1 + rightSetCapacity)
                    .mapToObj(j -> new Vertex( RIGHT_PREFIX + j, Constants.EMPTY, Randomizer.randomPositiveSmallInt()))
                    .forEach(graph::addRightVertex);
            IntStream.range(1, 1 + edgesAmount)
                    .mapToObj(j -> new Edge(
                            LEFT_PREFIX + Randomizer.randomIntBetween(1, leftSetCapacity),
                            RIGHT_PREFIX + Randomizer.randomIntBetween(1, rightSetCapacity),
                            Randomizer.randomPositiveSmallInt()))
                    .forEach(graph::addEdge);
            graphs.add(graph);
        }
        return graphs;
    }
}
