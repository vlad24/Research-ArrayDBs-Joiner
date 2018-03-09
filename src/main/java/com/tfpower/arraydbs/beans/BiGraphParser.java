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

    abstract protected BiGraphParseConfig constructConfig(String line) throws ParseException;

    abstract protected boolean isConfigLine(String line);

    abstract protected boolean isLineToIgnore(String line);

    abstract protected void fillBiGraph(List<String> contents, BiGraphParseConfig config, BiGraph biGraph);

    public BiGraph buildFromFile(String fileName) throws IOException, ParseException {
        BiGraph biGraph = new BiGraph();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(fileName).getInputStream()));
        String line = null;
        int currentLine = 0;
        List<String> contents = new ArrayList<>();
        BiGraphParseConfig config = null;
        try {
            while ((line = reader.readLine()) != null) {
                if (isConfigLine(line)) {
                    config = constructConfig(line);
                } else if (!isLineToIgnore(line)) {
                    contents.add(line);
                }
                currentLine++;
            }
            fillBiGraph(contents, config, biGraph);
        } catch (ParseException e){
            logger.error("Error at line {} : {}", currentLine, e.getMessage());
            throw e;
        }
        return biGraph;
    }


    public static class BiGraphParseConfig {
        private String firstClassPrefix;
        private String secondClassPrefix;
        private Map<String, Object> additionalParams;

        public String getSecondClassPrefix() {
            return secondClassPrefix;
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
    }

}
