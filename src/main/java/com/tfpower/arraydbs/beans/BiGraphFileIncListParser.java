package com.tfpower.arraydbs.beans;

import com.tfpower.arraydbs.beans.impl.BiGraphImpl;
import com.tfpower.arraydbs.domain.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    BufferedReader getFileReader(String fileName) throws IOException {
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
    BiGraph fillBiGraph(List<String> contents, BiGraphParseConfig config, BiGraphImpl biGraph) {

        for (String currentContentLine : contents){
            String contentLine = currentContentLine.replace(commentPrefix, "");
            String[] elements = contentLine.split(elementSepRegexp);
            String nodeDeclaration = elements[0];
            String[] neighbours = elements[1].split(neighbourSepRegexp);
            Vertex vertex = parseVertex(nodeDeclaration);
            biGraph.addFirstClassVertex(vertex);
        }
        List<Integer> row = contents.stream().map(c - >
                Arrays.stream(line.replace(commentPrefix, "").split(separatorRe))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList())
        ).;
        return null;
    }

    private Vertex parseVertex(String nodeDeclaration) {

        return null;
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
