package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.BiGraphProvider;
import com.tfpower.arraydbs.beans.BiGraphParser;
import com.tfpower.arraydbs.entity.BiGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;

@Component
public class BGraphProviderFromFileImpl implements BiGraphProvider {

    @Value("${inc_lists_graph_file_name}")
    private String fileName;

    @Autowired
    BiGraphParser parser;

    @Override
    public BiGraph getTestGraph() {
        try {
            return parser.buildFromFile(fileName);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
