package com.tfpower.arraydbs.entity;

import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.BiGraphProvider;
import com.tfpower.arraydbs.util.CSVUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ExperimentConductor {

    private final static Logger logger = LoggerFactory.getLogger(ExperimentConductor.class);

    public static List<String> conductExperiments(ArrayJoiner baseJoiner, List<ArrayJoiner> rivalJoiners, BiGraphProvider testDataProvider) {
        List<BiGraph> testGraphs = testDataProvider.getTestGraphs();
        List<String> csvResultRows = new ArrayList<>(1 + testGraphs.size());
        boolean csvHeaderFormed = false;
        for (ArrayJoiner rivalJoiner : rivalJoiners) {
            for (BiGraph testGraph : testGraphs) {
                JoinReport joinReportBase = baseJoiner.join(testGraph);
                logger.debug("Graph :\n{}", testGraph.toString());
                JoinReport joinReportRival = rivalJoiner.join(testGraph);
                JoinReport.JoinReportDiff joinDiff = JoinReport.diff(joinReportBase, joinReportRival);
                if (!csvHeaderFormed) {
                    csvResultRows.add(CSVUtil.concat(CSVUtil.asCsvRow(joinDiff.csvHeaderElements()), CSVUtil.asCsvRow(testGraph.description().csvHeaderElements())));
                    csvHeaderFormed = true;
                }
                String rivalCsvRow = CSVUtil.concat(CSVUtil.asCsvRow(joinDiff.csvElements()), CSVUtil.asCsvRow(testGraph.description().csvElements()));
                logger.debug("Join base : {}", joinReportBase.toString());
                logger.debug("Join rival: {}", joinReportRival.toString());
                csvResultRows.add(rivalCsvRow);
            }
        }
        return csvResultRows;
    }
}