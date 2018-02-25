package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.BiGraph;
import com.tfpower.arraydbs.domain.JoinReport;
import com.tfpower.arraydbs.domain.Vertex;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by vlad on 24.01.18.
 */
@Component
public class ArrayJoinerImpl implements ArrayJoiner {

    public JoinReport join(BiGraph bGraph) {
        // TODO implement something smart
        List<Vertex> leftVertices = new ArrayList<>(bGraph.getLeftVertices());
        List<Vertex> rightVertices = new ArrayList<>(bGraph.getLeftVertices());
        JoinReport report = new JoinReport();
        List<Integer> fakeVertices = IntStream
                .range(0, 2 * Math.min(leftVertices.size(), rightVertices.size()))
                .map(i -> i % 2 == 0 ? leftVertices.get(i / 2).getId() : rightVertices.get(i / 2).getId())
                .boxed()
                .collect(toList());
        report.setTraverseSequence(fakeVertices);
        return report;
    }
}
