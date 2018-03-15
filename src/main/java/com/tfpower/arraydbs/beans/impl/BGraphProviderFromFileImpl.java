package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.BiGraphParser;
import com.tfpower.arraydbs.beans.BiGraphProvider;
import com.tfpower.arraydbs.entity.BiGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Primary
public class BGraphProviderFromFileImpl implements BiGraphProvider {

    @Value("${graphs.from_inc_lists_files.names:test_graph.txt}")
    private String fileNames;

    @Autowired
    BiGraphParser parser;

    @Override
    public List<BiGraph> getTestGraphs() {
        try {
            ArrayList<BiGraph> list = new ArrayList<>();
            for (String fileNames : fileNames.split(",")) {
                BiGraph biGraph = parser.buildFromFile(fileNames.trim());
                list.add(biGraph);
            }
            return list;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
