package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.Cache;
import com.tfpower.arraydbs.entity.BiGraph;
import com.tfpower.arraydbs.entity.JoinReport;
import com.tfpower.arraydbs.entity.TraverseHelper;
import com.tfpower.arraydbs.entity.Vertex;
import com.tfpower.arraydbs.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.tfpower.arraydbs.entity.TraverseHelper.Status.DONE;

/**
 * Created by vlad on 24.01.18.
 */
@Component
public class ArrayJoinerCacheNaiveImpl implements ArrayJoiner {

    private final static Logger logger = LoggerFactory.getLogger(ArrayJoinerCacheNaiveImpl.class);

    @Autowired
    Cache<Vertex> cache;

    public JoinReport join(BiGraph bGraph) {
        Pair<Set<Vertex>, Set<Vertex>> prioritize = prioritizeSets(bGraph);
        Set<Vertex> smallestVertexSet = prioritize.getLeft();
        Set<Vertex> biggerVertexSet = prioritize.getRight();
        cache.clear();
        TraverseHelper traverse = new TraverseHelper();
        traverse.setAccumulatorUpdater((acc, vertex) -> acc + vertex.getWeight());
        fillCache(smallestVertexSet, traverse);
        for (Vertex current : smallestVertexSet){
            Set<Vertex> neighbours = bGraph.getNeighbours(current);
            for (Vertex neighbour : neighbours){
                if (!cache.contains(neighbour)){
                    Optional<Vertex> evicted = cache.loadOrEvict(neighbour, Cache.youngest());
                    assert !evicted.isPresent() || biggerVertexSet.contains(evicted.get());
                    traverse.pushToVisitResult(neighbour);
                    traverse.accountVisit(neighbour);
                }
                traverse.markEdge(bGraph.getExistingEdge(current, neighbour), DONE);
            }
        }
        assert bGraph.getAllEdges().stream().map(traverse::statusOfEdge).allMatch(status -> status.equals(DONE)) : "Not all edges are processed";
        return JoinReport.fromGraphTraversal(traverse, this.toString(), bGraph.description());
    }


    private void fillCache(Set<Vertex> smallestVertexSet, TraverseHelper traverse) {
        int spareSpace = cache.getCapacity() - smallestVertexSet.size();
        if (spareSpace > 0){
            // load the entire operand
            for (Vertex vertex : smallestVertexSet){
                cache.loadOrFail(vertex);
                traverse.pushToVisitResult(vertex);
                traverse.accountVisit(vertex);
            }
        } else {
            // load only part of it leaving space for one more from another operand
            Iterator<Vertex> vertexIterator = smallestVertexSet.iterator();
            for (int i = 0; i < cache.getCapacity() - 1 && vertexIterator.hasNext(); i++){
                Vertex vertex = vertexIterator.next();
                cache.loadOrFail(vertex);
                traverse.pushToVisitResult(vertex);
                traverse.accountVisit(vertex);
            }
        }
    }


    private Pair<Set<Vertex>, Set<Vertex>> prioritizeSets(BiGraph bGraph) {
        List<Set<Vertex>> sets = Arrays.asList(bGraph.getLeftVertices(), bGraph.getRightVertices());
        sets.sort(Comparator.comparingInt(Set::size));
        return new Pair<>(sets.get(0), sets.get(1));
    }

    @Override
    public String toString() {
        return "naive-joiner<" + cache.getCapacity() + ">";
    }

}
