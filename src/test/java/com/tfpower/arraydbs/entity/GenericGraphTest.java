package com.tfpower.arraydbs.entity;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import com.tfpower.arraydbs.util.Constants;
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
        testGraph.addEdge(new Edge("3-2", "3", "2", 32, Constants.EMPTY));
        testGraph.addEdge(new Edge("3-6", "3", "6", 36, Constants.EMPTY));
        testGraph.addEdge(new Edge("5-4", "5", "4", 54, Constants.EMPTY));

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
    public void computeReachability() throws Exception {
        Map<String, Set<String>> reachMap = testGraph.computeReachability(2);
        assertEquals(reachMap.get("1"), new HashSet<>(Arrays.asList("1", "2", "3", "4", "5")));
        assertEquals(reachMap.get("2"), new HashSet<>(Arrays.asList("2", "1", "3", "4", "6")));
        assertEquals(reachMap.get("3"), new HashSet<>(Arrays.asList("3", "2", "6", "1")));
        assertEquals(reachMap.get("4"), new HashSet<>(Arrays.asList("4", "1", "5", "2")));
        assertEquals(reachMap.get("5"), new HashSet<>(Arrays.asList("5", "4", "1")));
        assertEquals(reachMap.get("6"), new HashSet<>(Arrays.asList("6", "3", "2")));

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