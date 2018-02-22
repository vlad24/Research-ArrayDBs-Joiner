package com.tfpower.arraydbs.beans;

import com.tfpower.arraydbs.beans.impl.BiGraphImpl;
import com.tfpower.arraydbs.domain.Vertex;
import com.tfpower.arraydbs.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Created by vlad on 24.01.18.
 */
@Component
public class BiGraphFileIncListParser extends BiGraphParser{

    private  static  final Logger logger = LoggerFactory.getLogger(BiGraphFileIncListParser.class);

    public static final String configPrefix = "@";
    public static final String commentPrefix = "@";
    public static final String firstClassRegexp = "firstClassPrefix=(.*)";
    public static final String secondClassRegexp = "secondClassPrefix=(.*)";
    public static final String elementSepRegexp="\\s+";
    public static final String neighbourSepRegexp = "->";

    public BiGraphFileIncListParser() {
    }


    @Override
    BufferedReader getFileReader() throws IOException {
        return new BufferedReader(new InputStreamReader(new ClassPathResource(fileName).getInputStream()));
    }

    @Override
    boolean isConfigLine(String line) {
        return line.startsWith(configPrefix);
    }

    @Override
    boolean isCommentLine(String line) {
        return line.startsWith(commentPrefix);
    }

    @Override
    BiGraphParseConfig constructConfig(String configLine) throws ParseException {
        BiGraphParseConfig config = new BiGraphParseConfig();
        config.setFirstClassPrefix(getSubstringMatchingRegex(firstClassRegexp, configLine, "A"));
        config.setSecondClassPrefix(getSubstringMatchingRegex(secondClassRegexp, configLine, "B"));
        return config;
    }

    @Override
    void fillBiGraph(List<String> contents, BiGraphParseConfig config, final BiGraph biGraph) {
        String firstClassVerticesRaw = contents.get(0);
        String secondClassVerticesRaw = contents.get(1);
        Arrays.stream(firstClassVerticesRaw.split(elementSepRegexp)).forEach(p -> biGraph.addFirstClassVertex(parseVertex(p)));
        Arrays.stream(secondClassVerticesRaw.split(elementSepRegexp)).forEach(p -> biGraph.addSecondClassVertex(parseVertex(p)));
        List<Pair<Integer, List<Integer>>> neighbours = contents.stream().skip(2).map(this::parseNeighbours).collect(toList());
        for (Pair<Integer, List<Integer>> neighbourInfo : neighbours){
            neighbourInfo.getRight().forEach(n -> biGraph.addEdge(neighbourInfo.getLeft(), n));
        }
    }

    private Vertex parseVertex(String nodeDeclaration) {
        //TODO
        return null;
    }

    private Pair<Integer, List<Integer>> parseNeighbours(String line){
        String parts[] = line.split(neighbourSepRegexp);
        return new Pair<>(Integer.parseInt(parts[0]), Arrays.stream(parts[1].split(elementSepRegexp)).map(Integer::parseInt).collect(toList()));
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
