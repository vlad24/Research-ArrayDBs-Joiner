package com.tfpower.arraydbs.entity;

import com.tfpower.arraydbs.util.Constants;
import com.tfpower.arraydbs.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class GenericGraphTest {

    private GenericGraph testGraph;

    @Before
    public void buildTestGraphs(){
        testGraph = new GenericGraph();
        for (int i = 1; i <= 6; i++) {
            testGraph.addVertex(new Vertex(String.valueOf(i)));
        }
        testGraph.addEdge(new Edge("1-2", "1", "2", 12, Constants.EMPTY));
        testGraph.addEdge(new Edge("1-4", "1", "4", 14, Constants.EMPTY));
        testGraph.addEdge(new Edge("2-3", "3", "2", 32, Constants.EMPTY));
        testGraph.addEdge(new Edge("3-6", "3", "6", 36, Constants.EMPTY));
        testGraph.addEdge(new Edge("4-5", "5", "4", 54, Constants.EMPTY));
    }


    @Test
    public void addVertex() throws Exception {
    }


    @Test
    public void addEdge() throws Exception {
    }


    @Test
    public void isEdgeAllowed_WhenNonexistingEdgeAdded_ThenEdgeIsNotAllowed() throws Exception {
        Set<String> allVerticesIds = testGraph.getAllVerticesIds();
        String longestId = allVerticesIds.stream().max(Comparator.comparingInt(String::length)).orElse("");
        assertFalse(testGraph.isEdgeAllowed(new Edge("nonexistingVertex-" + longestId, longestId, Constants.ABSENT)));
    }


    @Test
    public void getAllEdges() throws Exception {
    }


    @Test
    public void getNeighbours() throws Exception {
    }


    @Test
    public void getNeighboursThat() throws Exception {
    }


    @Test
    public void getVertexSurrounding() throws Exception {
    }


    @Test
    public void getEdgeSurrounding() throws Exception {
    }


    @Test
    public void getVertexById() throws Exception {
    }


    @Test
    public void getEdgeBetween() throws Exception {
    }


    @Test
    public void getAllEdgesBetween() throws Exception {
    }


    @Test
    public void getAllEdgesBetween1() throws Exception {
    }


    @Test
    public void getIncidenceMap() throws Exception {
    }


    @Test
    public void getPathBetween() throws Exception {
    }


    @Test
    public void getAllVertices() throws Exception {
    }


    @Test
    public void getAllVerticesIds() throws Exception {
    }


    @Test
    public void getVertexAmount() throws Exception {
    }


    @Test
    public void getEdgeAmount() throws Exception {
    }


    @Test
    public void getEdgesAround() throws Exception {
    }


    @Test
    public void degree() throws Exception {
    }


    @Test
    public void computeReachValues_WhenLevelIsTwo_ThenSiblingsAndNeighboursAreRetuned() throws Exception {
        Map<String, Set<String>> reachMap = testGraph.computeReachSets(2);
        assertEquals(new HashSet<>(Arrays.asList("2", "3", "4", "5")),      reachMap.get("1"));
        assertEquals(new HashSet<>(Arrays.asList("1", "3", "4", "6")),      reachMap.get("2"));
        assertEquals(new HashSet<>(Arrays.asList("2", "6", "1")),           reachMap.get("3"));
        assertEquals(new HashSet<>(Arrays.asList("1", "5", "2")),           reachMap.get("4"));
        assertEquals(new HashSet<>(Arrays.asList("4", "1")),                reachMap.get("5"));
        assertEquals(new HashSet<>(Arrays.asList("3", "2")),                reachMap.get("6"));
    }

    @Test
    public void computeSubPathsValues_WhenLevelIsTwo_ThenSiblingsAndNeighboursEdgesAreReturned() throws Exception {
        Map<String, Set<String>> edgeSets = testGraph.computeEdgeSets(2);
        assertEquals(new HashSet<>(Arrays.asList("1-2", "1-4", "2-3", "4-5")),  edgeSets.get("1"));
        assertEquals(new HashSet<>(Arrays.asList("1-2", "2-3", "1-4", "3-6")),  edgeSets.get("2"));
        assertEquals(new HashSet<>(Arrays.asList("2-3", "1-2", "3-6")),         edgeSets.get("3"));
        assertEquals(new HashSet<>(Arrays.asList("1-4", "4-5", "1-2")),         edgeSets.get("4"));
        assertEquals(new HashSet<>(Arrays.asList("4-5", "1-4")),                edgeSets.get("5"));
        assertEquals(new HashSet<>(Arrays.asList("3-6", "2-3")),                edgeSets.get("6"));
    }


    @Test
    public void computeReachValues_WhenLevelIstVeryHigh_AllVerticesAreReturned() throws Exception {
        final int level = testGraph.getEdgeAmount();
        Set<String> allVerticesIds = testGraph.getAllVerticesIds();
        Map<String, Set<String>> reachMap = testGraph.computeReachSets(level);
        for (String examinedVertex : allVerticesIds) {
            HashSet<String> expected = new HashSet<>(allVerticesIds);
            expected.remove(examinedVertex);
            assertEquals(expected, reachMap.get(examinedVertex));
        }
    }

    @Test
    public void computeReachValues_WhenLevelIsOne_ThenNeighboursReturned() throws Exception {
        Map<String, Set<String>> reachMap = testGraph.computeReachSets(1);
        for (String vertex : testGraph.getAllVerticesIds()) {
            assertEquals(testGraph.getNeighboursIds(vertex), reachMap.get(vertex));
        }
    }

    @Test
    public void computeReachValues_WhenLevelIsZero_TheVertexIsReturned() throws Exception {
        Map<String, Set<String>> reachMap = testGraph.computeReachSets(0);
        for (String vertex : testGraph.getAllVerticesIds()) {
            assertEquals(Collections.singleton(vertex), reachMap.get(vertex));
        }
    }


    @Test
    public void getExistingEdge() throws Exception {
    }


    @Test
    public void getPathBetween1() throws Exception {
    }


    @Test
    public void getEdgeBetween1() throws Exception {
    }


    @Test
    public void areConnected() throws Exception {
    }


    @Test
    public void areConnected1() throws Exception {
    }


    @Test
    public void getExistingVertex() throws Exception {
    }


    @Test
    public void getNeighbours1() throws Exception {
    }


    @Test
    public void getNeighboursThat1() throws Exception {
    }


    @Test
    public void degree1() throws Exception {
    }


    @Test
    public void copy() throws Exception {
    }



}