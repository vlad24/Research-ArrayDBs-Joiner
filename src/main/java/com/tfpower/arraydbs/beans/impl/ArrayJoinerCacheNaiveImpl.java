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

    //TODO test
    public JoinReport join(BiGraph bGraph) {
        Pair<Set<Vertex>, Set<Vertex>> prioritize = prioritizeSets(bGraph);
        Set<Vertex> smallestVertexSet = prioritize.getLeft();
        Set<Vertex> biggerVertexSet = prioritize.getRight();
        cache.clear();
        TraverseHelper traverse = new TraverseHelper();
        traverse.setAccumulatorUpdater((acc, vertex) -> acc + vertex.getWeight());
        fillCache(smallestVertexSet, traverse);
        for (Vertex anchorVertex : smallestVertexSet){
            if (!cache.contains(anchorVertex)){
                cache.loadOrEvict(anchorVertex,
                        Comparator.comparingInt((Cache.CacheEntry<Vertex> o) -> smallestVertexSet.contains(o.getValue()) ? 1 : 0)
                                .thenComparing(Cache.byAge())
                );
                traverse.accountVertexVisit(anchorVertex);
            }
            Set<Vertex> neighbours = bGraph.getNeighbours(anchorVertex);
            for (Vertex neighbour : neighbours){
                if (!cache.contains(neighbour)){
                    Optional<Vertex> evicted = cache.loadOrEvict(neighbour,
                            Comparator.comparingInt((Cache.CacheEntry<Vertex> o) -> smallestVertexSet.contains(o.getValue()) ? 0 : 1)
                                    .thenComparing(Cache.byAge())
                    );
                    assert !evicted.isPresent() || biggerVertexSet.contains(evicted.get());
                    traverse.accountVertexVisit(neighbour);
                }
                traverse.markEdge(bGraph.getExistingEdge(anchorVertex, neighbour), DONE);
            }
        }
        assert bGraph.getAllEdges().stream().map(traverse::statusOfEdge).allMatch(status -> status.equals(DONE)) : "Not all edges are processed";
        return JoinReport.fromGraphTraversal(traverse, this.toString(), bGraph.description());
    }


    private void fillCache(Set<Vertex> smallestVertexSet, TraverseHelper traverse) {
        int spareSpace = cache.getCapacity() - smallestVertexSet.size();
        int loopLimit = spareSpace > 0 ? smallestVertexSet.size() : cache.getCapacity() - 1;
        Iterator<Vertex> vertexIterator = smallestVertexSet.iterator();
        for (int i = 0; i < loopLimit && vertexIterator.hasNext(); i++){
            Vertex vertex = vertexIterator.next();
            cache.loadOrFail(vertex);
            traverse.accountVertexVisit(vertex);
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
