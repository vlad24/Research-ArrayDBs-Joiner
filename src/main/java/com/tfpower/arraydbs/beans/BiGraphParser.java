package com.tfpower.arraydbs.beans;

import com.tfpower.arraydbs.beans.impl.BiGraphImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
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

    abstract BiGraphParseConfig constructConfig(String line) throws ParseException;

    abstract BufferedReader getFileReader() throws IOException;

    abstract boolean isConfigLine(String line);

    abstract boolean isCommentLine(String line);

    abstract BiGraph fillBiGraph(List<String> contents, BiGraphParseConfig config, BiGraphImpl biGraph);

    public BiGraph buildFromFile(BiGraphImpl biGraph) throws IOException, ParseException {
        BufferedReader reader = getFileReader();
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
            return fillBiGraph(contents, config, biGraph);
        } catch (ParseException e){
            logger.error("Error at line {} : {}", currentLine, e.getMessage());
            throw e;
        }
    }


}
