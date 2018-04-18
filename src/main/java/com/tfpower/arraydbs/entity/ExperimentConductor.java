package com.tfpower.arraydbs.entity;

import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.BiGraphProvider;
import com.tfpower.arraydbs.util.CSVUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExperimentConductor {

    private final static Logger logger = LoggerFactory.getLogger(ExperimentConductor.class);

    public static Map<String, Object> conductExperiments(ArrayJoiner baseJoiner, List<ArrayJoiner> rivalJoiners, BiGraphProvider testDataProvider) {
        List<BiGraph> testGraphs = testDataProvider.getTestGraphs();
        List<String> csvResultRows = new ArrayList<>(1 + testGraphs.size());
        boolean csvHeaderFormed = false;
        Map<String, Object> result = new HashMap<>();
        for (ArrayJoiner rivalJoiner : rivalJoiners) {
            List<BigDecimal> loadAmounts = new ArrayList<>();
            List<BigDecimal> maxLoadAmounts = new ArrayList<>();
            for (BiGraph testGraph : testGraphs) {
                JoinReport joinReportBase = baseJoiner.join(testGraph);
                logger.debug("Graph :\n{}", testGraph.toString());
                JoinReport joinReportRival = rivalJoiner.join(testGraph);
                JoinReport.JoinReportDiff joinDiff = JoinReport.diff(joinReportBase, joinReportRival);
                loadAmounts.add(joinDiff.getRatioLoadAmount());
                maxLoadAmounts.add(joinDiff.getRatioMaxLoad());
                if (!csvHeaderFormed) {
                    csvResultRows.add(CSVUtil.concat(CSVUtil.asCsvRow(joinDiff.csvHeaderElements()), CSVUtil.asCsvRow(testGraph.description().csvHeaderElements())));
                    csvHeaderFormed = true;
                }
                String rivalCsvRow = CSVUtil.concat(CSVUtil.asCsvRow(joinDiff.csvElements()), CSVUtil.asCsvRow(testGraph.description().csvElements()));
                logger.debug("Join base : {}", joinReportBase.toString());
                logger.debug("Join rival: {}", joinReportRival.toString());
                csvResultRows.add(rivalCsvRow);
            }
            result.put("loadAmountAvg_" + rivalJoiner.toString(), loadAmounts9.stream().reduce(BigDecimal::add).9)
        }

        return csvResultRows;
    }
}