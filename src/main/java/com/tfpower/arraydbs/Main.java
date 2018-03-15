package com.tfpower.arraydbs;

import ch.qos.logback.core.util.FileUtil;
import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.BiGraphProvider;
import com.tfpower.arraydbs.beans.impl.ArrayJoinerCacheEulerImpl;
import com.tfpower.arraydbs.beans.impl.ArrayJoinerCacheHeuristicsImpl;
import com.tfpower.arraydbs.beans.impl.ArrayJoinerCacheNaiveImpl;
import com.tfpower.arraydbs.beans.impl.BGraphProviderByRandomImpl;
import com.tfpower.arraydbs.config.AppConfig;
import com.tfpower.arraydbs.entity.BiGraph;
import com.tfpower.arraydbs.entity.JoinReport;
import com.tfpower.arraydbs.util.CSVUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.FileSystemUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * Created by vlad on 24.01.18.
 */
public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        BiGraphProvider biGraphProvider = context.getBean(BGraphProviderByRandomImpl.class);
        List<BiGraph> testGraphs = biGraphProvider.getTestGraphs();
        ArrayJoiner arrayJoinerRival = context.getBean(ArrayJoinerCacheHeuristicsImpl.class);
        ArrayJoiner arrayJoinerBase  = context.getBean(ArrayJoinerCacheEulerImpl.class);
        List<String> csvResult = new ArrayList<>(1 + testGraphs.size());
        boolean csvHeaderFormed = false;
        for (BiGraph testGraph : testGraphs) {
            logger.info("Joining {}", testGraph.getName());
            JoinReport joinReportBase  = arrayJoinerBase.join(testGraph);
            JoinReport joinReportRival = arrayJoinerRival.join(testGraph);
            JoinReport.JoinReportDiff joinDiff = JoinReport.diff(joinReportBase, joinReportRival);
            if (!csvHeaderFormed){
                csvResult.add(CSVUtil.concat(CSVUtil.asCsvRow(joinDiff.csvHeaderElements()), CSVUtil.asCsvRow(testGraph.description().csvHeaderElements())));
                csvHeaderFormed = true;
            }
            csvResult.add(CSVUtil.concat(CSVUtil.asCsvRow(joinDiff.csvElements()), CSVUtil.asCsvRow(testGraph.description().csvElements())));
            logger.debug("Join base : {}", joinReportBase.toString());
            logger.debug("Join rival: {}", joinReportRival.toString());
        }

        logger.info("Program is over. CSV diffs:\n\n{}", csvResult.stream().collect(joining("\n")));
    }

}
