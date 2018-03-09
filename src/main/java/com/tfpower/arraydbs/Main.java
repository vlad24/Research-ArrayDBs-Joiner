package com.tfpower.arraydbs;

import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.BiGraph;
import com.tfpower.arraydbs.config.AppConfig;
import com.tfpower.arraydbs.domain.JoinReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by vlad on 24.01.18.
 */
public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ArrayJoiner arrayJoiner = context.getBean(ArrayJoiner.class);
        BiGraph bGraph = context.getBean(BiGraph.class);
        JoinReport joinReport = arrayJoiner.join(bGraph);
        logger.debug("Join process is over: {}",  joinReport);
        logger.info("Explaining: {}", joinReport.getLoadFrequencies().values().stream().mapToInt(Integer::intValue).summaryStatistics());
    }

}
