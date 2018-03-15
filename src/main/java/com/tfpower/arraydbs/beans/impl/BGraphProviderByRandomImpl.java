package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.BiGraphProvider;
import com.tfpower.arraydbs.entity.BiGraph;
import com.tfpower.arraydbs.entity.Edge;
import com.tfpower.arraydbs.entity.Vertex;
import com.tfpower.arraydbs.util.Constants;
import com.tfpower.arraydbs.util.Randomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Component
public class BGraphProviderByRandomImpl implements BiGraphProvider {

    private final Logger logger = LoggerFactory.getLogger(BGraphProviderByRandomImpl.class);

    private final String LEFT_PREFIX = "A";
    private final String RIGHT_PREFIX = "B";

    @Value("${graphs.random.amount}")
    private Integer graphsCount;

    @Value("${graphs.random.left_set.capacity:10}")
    private Integer leftSetCapacity;


    @Value("${graphs.random.right_set.capacity:10}")
    private Integer rightSetCapacity;

    @Value("${graphs.random.min_edge_gen_attempts:100}")
    private Integer minEdgesGenerateAttempts;


    @Override
    public List<BiGraph> getTestGraphs() {
        logger.info("Generating {} test graphs", graphsCount);
        List<BiGraph> graphList = new ArrayList<>(graphsCount);
        for (int i = 0; i < graphsCount; i++) {
            BiGraph graph = new BiGraph("Graph_" + i);
            IntStream.range(1, 1 + leftSetCapacity)
                    .mapToObj(j -> new Vertex(LEFT_PREFIX + j, Constants.EMPTY, Randomizer.randomPositiveSmallInt()))
                    .forEach(graph::addLeftVertex);
            IntStream.range(1, 1 + rightSetCapacity)
                    .mapToObj(j -> new Vertex(RIGHT_PREFIX + j, Constants.EMPTY, Randomizer.randomPositiveSmallInt()))
                    .forEach(graph::addRightVertex);
            Set<Set<String>> edgesNibs = new HashSet<>(minEdgesGenerateAttempts);
            Set<String> connectedVertices = new HashSet<>();
            boolean resumeFromLeft = true;
            String vertexToResumeFrom = Randomizer.pickRandomFrom(graph.getLeftVertices()).getId();
            int edgeGenAttempts = 0;
            while (edgeGenAttempts < minEdgesGenerateAttempts || connectedVertices.size() < graph.getVertexAmount()) {
                String currentLeft = LEFT_PREFIX + Randomizer.randomIntBetween(1, leftSetCapacity);
                String currentRight = RIGHT_PREFIX + Randomizer.randomIntBetween(1, rightSetCapacity);
                if (resumeFromLeft){
                    currentLeft = vertexToResumeFrom;
                } else {
                    currentRight = vertexToResumeFrom;
                }
                Edge edge = new Edge(currentLeft, currentRight, Randomizer.randomPositiveSmallInt());
                Set<String> edgeNibs = edge.nibs();
                if (!edgesNibs.contains(edgeNibs)){
                    edgesNibs.add(edgeNibs);
                    graph.addEdge(edge);
                }
                resumeFromLeft = Randomizer.randomBoolean();
                vertexToResumeFrom = resumeFromLeft ? currentLeft : currentRight;
                connectedVertices.add(vertexToResumeFrom);
                edgeGenAttempts++;
            }

            graphList.add(graph);
        }
        return graphList;
    }
}
