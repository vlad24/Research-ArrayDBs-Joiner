package com.tfpower.arraydbs.beans;

import com.tfpower.arraydbs.beans.impl.BiGraphImpl;
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

/**
 * Created by vlad on 24.01.18.
 */
@Component
public abstract class BiGraphParser {

    private static final Logger logger = LoggerFactory.getLogger(BiGraphParser.class);

    public BiGraphParser() {
    }

    abstract protected  BiGraphParseConfig constructConfig(String line) throws ParseException;

    abstract protected  String getFileName() throws IOException;

    abstract protected boolean isConfigLine(String line);

    abstract protected boolean isCommentLine(String line);

    abstract protected  void fillBiGraph(List<String> contents, BiGraphParseConfig config, BiGraph biGraph);

    public void buildFromFile(BiGraphImpl biGraph) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(getFileName()).getInputStream()));
        String line = null;
        int currentLine = 0;
        List<String> contents = new ArrayList<>();
        BiGraphParseConfig config = null;
        try {
            while ((line = reader.readLine()) != null) {
                if (isConfigLine(line)) {
                    config = constructConfig(line);
                } else if (!isCommentLine(line)) {
                    contents.add(line);
                }
                currentLine++;
            }
            fillBiGraph(contents, config, biGraph);
        } catch (ParseException e){
            logger.error("Error at line {} : {}", currentLine, e.getMessage());
            throw e;
        }
    }


}
