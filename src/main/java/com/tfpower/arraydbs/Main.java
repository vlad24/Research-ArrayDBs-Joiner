package com.tfpower.arraydbs;

import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.BiGraphProvider;
import com.tfpower.arraydbs.config.AppConfig;
import com.tfpower.arraydbs.entity.BiGraph;
import com.tfpower.arraydbs.entity.JoinReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by vlad on 24.01.18.
 */
public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        BiGraphProvider biGraphProvider = context.getBean(BiGraphProvider.class);
        BiGraph testGraph = biGraphProvider.getTestGraph();
        ArrayJoiner arrayJoinerByCacheHeuristics = context.getBean(ArrayJoiner.class);
        JoinReport joinReport = arrayJoinerByCacheHeuristics.join(testGraph);
        logger.debug("Join process is over: {}",  joinReport);
        logger.info("Explaining: {}", joinReport.getLoadFrequencies().values()
                .stream().mapToInt(Integer::intValue).summaryStatistics());
    }

}
