package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.BiGraphParser;
import com.tfpower.arraydbs.entity.BiGraph;
import com.tfpower.arraydbs.entity.Edge;
import com.tfpower.arraydbs.entity.Vertex;
import com.tfpower.arraydbs.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Created by vlad on 24.01.18.
 */
@Component
public class BiGraphFileIncListParser extends BiGraphParser {

    private  static  final Logger logger = LoggerFactory.getLogger(BiGraphFileIncListParser.class);

    public static final String CONFIG_PREFIX = "@";
    public static final String COMMENT_PREFIX = "#";
    public static final String FIRST_CLASS_PREFIX = "firstClassPrefix=(.*)";
    public static final String SECOND_CLASS_PREFIX = "secondClassPrefix=(.*)";
    public static final String WEIGHT_SEP_REGEXP ="_";
    public static final String ELEMENT_SEP_REGEXP ="\\s+";
    public static final String NEIGHBOUR_SEP_REGEXP = "->";

    public BiGraphFileIncListParser() {
    }

    @Override
    protected boolean isConfigLine(String line) {
        return line.startsWith(CONFIG_PREFIX);
    }

    @Override
    protected boolean isLineToIgnore(String line) {
        return line == null || line.trim().isEmpty() || line.startsWith(COMMENT_PREFIX);
    }

    @Override
    protected BiGraphParseConfig constructConfig(String configLine) throws ParseException {
        BiGraphParseConfig config = new BiGraphParseConfig();
        config.setFirstClassPrefix(getSubstringMatchingRegex(FIRST_CLASS_PREFIX, configLine, "A"));
        config.setSecondClassPrefix(getSubstringMatchingRegex(SECOND_CLASS_PREFIX, configLine, "B"));
        return config;
    }

    @Override
    protected void fillBiGraph(List<String> contents, BiGraphParseConfig config, final BiGraph biGraph) {
        String firstClassVerticesRaw = contents.get(0).trim();
        String secondClassVerticesRaw = contents.get(1).trim();
        Arrays.stream(firstClassVerticesRaw.split(ELEMENT_SEP_REGEXP)).forEach(p -> biGraph.addLeftVertex(parseVertex(p)));
        Arrays.stream(secondClassVerticesRaw.split(ELEMENT_SEP_REGEXP)).forEach(p -> biGraph.addRightVertex(parseVertex(p)));
        List<Pair<String, List<String>>> links = contents.stream().skip(2).map(this::parseLinks).collect(toList());
        links.forEach(link ->
                link.getRight().forEach(element ->
                        biGraph.addEdge(new Edge(link.getLeft(), element,
                                biGraph.getVertex(link.getLeft()).get().getWeight()
                                        + biGraph.getVertex(element).get().getWeight()
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


    private String getSubstringMatchingRegex(String regex, String source, String defaultValue) {
        logger.trace("Matching {} against {}", source, regex);
        Matcher matcher = Pattern.compile(regex).matcher(source);
        if (matcher.groupCount() > 1) {
            return matcher.group(1);
        } else {
            return defaultValue;
        }
    }


}
