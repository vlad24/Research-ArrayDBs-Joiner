package com.tfpower.arraydbs;

import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.BiGraphProvider;
import com.tfpower.arraydbs.config.AppConfig;
import com.tfpower.arraydbs.entity.BiGraph;
import com.tfpower.arraydbs.entity.JoinReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

/**
 * Created by vlad on 24.01.18.
 */
public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        BiGraphProvider biGraphProvider = context.getBean(BiGraphProvider.class);
        List<BiGraph> testGraphs = biGraphProvider.getTestGraphs();
        ArrayJoiner arrayJoiner = context.getBean(ArrayJoiner.class);
        for (BiGraph testGraph : testGraphs) {
            JoinReport joinReport = arrayJoiner.join(testGraph);
            logger.info("Join process is over: {}", joinReport);
            logger.info("Load stats: {}", joinReport.getLoadFrequencies().values()
                    .stream().mapToInt(Integer::intValue).summaryStatistics());
        }
    }

}
