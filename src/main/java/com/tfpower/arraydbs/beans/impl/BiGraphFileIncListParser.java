package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.BiGraphParser;
import com.tfpower.arraydbs.entity.BiGraph;
import com.tfpower.arraydbs.entity.Edge;
import com.tfpower.arraydbs.entity.Vertex;
import com.tfpower.arraydbs.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

/**
 * Created by vlad on 24.01.18.
 */
@Component
public class BiGraphFileIncListParser extends BiGraphParser {

    private  static  final Logger logger = LoggerFactory.getLogger(BiGraphFileIncListParser.class);

    private static final String CONFIG_PREFIX = "@";
    private static final String COMMENT_PREFIX = "#";
    private static final String FIRST_CLASS_RE = ".*firstClassPrefix=(.*?);.*";
    private static final String SECOND_CLASS_RE = ".*secondClassPrefix=(.*?);.*";
    private static final String GRAPH_NAME_REGEX = ".*graphName=(.*?);.*";
    private static final String WEIGHT_SEP_REGEXP ="_";
    private static final String ELEMENT_SEP_REGEXP ="\\s+";
    private static final String NEIGHBOUR_SEP_REGEXP = "->";

    @Override
    protected boolean isMetaInfoLine(String line) {
        return line.startsWith(CONFIG_PREFIX);
    }

    @Override
    protected boolean isLineToIgnore(String line) {
        return line == null || line.trim().isEmpty() || line.startsWith(COMMENT_PREFIX);
    }

    @Override
    protected BiGraphParseMetaInfo parseMetaInfo(String configLine){
        BiGraphParseMetaInfo config = new BiGraphParseMetaInfo();
        config.setGraphName(getSubstringByRegex(GRAPH_NAME_REGEX, configLine, "some_graph"));
        config.setFirstClassPrefix(getSubstringByRegex(FIRST_CLASS_RE, configLine, "A"));
        config.setSecondClassPrefix(getSubstringByRegex(SECOND_CLASS_RE, configLine, "B"));
        return config;
    }

    @Override
    protected void fillBiGraph(List<String> contents, BiGraphParseMetaInfo metaInfo, final BiGraph biGraph) {
        biGraph.setName(metaInfo.getGraphName());
        String firstClassVerticesRaw = contents.get(0).trim();
        String secondClassVerticesRaw = contents.get(1).trim();
        Arrays.stream(firstClassVerticesRaw.split(ELEMENT_SEP_REGEXP)).forEach(p -> biGraph.addLeftVertex(parseVertex(p)));
        Arrays.stream(secondClassVerticesRaw.split(ELEMENT_SEP_REGEXP)).forEach(p -> biGraph.addRightVertex(parseVertex(p)));
        List<Pair<String, List<String>>> links = contents.stream().skip(2).map(this::parseLinks).collect(toList());
        links.forEach(link ->
                link.getRight().forEach(element ->
                        biGraph.addEdge(new Edge(link.getLeft(), element,
                                biGraph.getExistingVertex(link.getLeft()).getWeight()
                                        + biGraph.getExistingVertex(element).getWeight()
                                ))
                ));
    }

    private Vertex parseVertex(String vertexDeclaration) {
        List<String> vertexDataParts = getTrimmedParts(vertexDeclaration, WEIGHT_SEP_REGEXP);
        return new Vertex(vertexDataParts.get(0), Integer.parseInt(vertexDataParts.get(1)));
    }

    private List<String> getTrimmedParts(String vertexDeclaration, String separator) {
        return Arrays.stream(vertexDeclaration.trim().split(separator)).map(String::trim).collect(toList());
    }

    private Pair<String, List<String>> parseLinks(String line){
        List<String> parts = getTrimmedParts(line, NEIGHBOUR_SEP_REGEXP);
        return new Pair<>(parts.get(0), Arrays.asList(parts.get(1).split(ELEMENT_SEP_REGEXP)));
    }


    private String getSubstringByRegex(String regex, String source, String defaultValue) {
        Matcher matcher = Pattern.compile(regex).matcher(source);
        if (matcher.matches() && matcher.groupCount() >= 1){
            return matcher.group(1);
        } else {
            return defaultValue;
        }
    }


}
