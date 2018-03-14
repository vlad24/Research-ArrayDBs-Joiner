package com.tfpower.arraydbs.beans;

import com.tfpower.arraydbs.entity.BiGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vlad on 24.01.18.
 */
@Component
public abstract class BiGraphParser {

    private static final Logger logger = LoggerFactory.getLogger(BiGraphParser.class);

    public BiGraphParser() {
    }

    abstract protected BiGraphParseMetaInfo parseMetaInfo(String line) throws ParseException;

    abstract protected boolean isMetaInfoLine(String line);

    abstract protected boolean isLineToIgnore(String line);

    abstract protected void fillBiGraph(List<String> contents, BiGraphParseMetaInfo config, BiGraph biGraph);


    public BiGraph buildFromFile(String fileName) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(fileName).getInputStream()));
        String line = null;
        int currentLine = 0;
        List<String> contents = new ArrayList<>();
        BiGraphParseMetaInfo config = null;
        try {
            while ((line = reader.readLine()) != null) {
                if (isMetaInfoLine(line)) {
                    config = parseMetaInfo(line);
                } else if (!isLineToIgnore(line)) {
                    contents.add(line);
                }
                currentLine++;
            }
            BiGraph biGraph = new BiGraph();
            fillBiGraph(contents, config, biGraph);
            return biGraph;
        } catch (ParseException e) {
            logger.error("Error at line {} : {}", currentLine, e.getMessage());
            throw e;
        }
    }


    public static class BiGraphParseMetaInfo {
        private String firstClassPrefix;
        private String secondClassPrefix;
        private Map<String, Object> additionalParams;
        private String graphName;


        public String getSecondClassPrefix() {
            return secondClassPrefix;
        }


        public String getGraphName() {
            return graphName;
        }


        public void setSecondClassPrefix(String secondClassPrefix) {
            this.secondClassPrefix = secondClassPrefix;
        }

        public String getFirstClassPrefix() {
            return firstClassPrefix;
        }

        public void setFirstClassPrefix(String firstClassPrefix) {
            this.firstClassPrefix = firstClassPrefix;
        }

        public Map<String, Object> getAdditionalParams() {
            return additionalParams;
        }

        public void setAdditionalParams(Map<String, Object> additionalParams) {
            this.additionalParams = additionalParams;
        }


        public void setGraphName(String graphName) {
            this.graphName = graphName;
        }
    }

}
