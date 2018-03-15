package com.tfpower.arraydbs;

import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.BiGraphProvider;
import com.tfpower.arraydbs.beans.impl.ArrayJoinerCacheEulerImpl;
import com.tfpower.arraydbs.beans.impl.ArrayJoinerCacheHeuristicsImpl;
import com.tfpower.arraydbs.beans.impl.ArrayJoinerCacheNaiveImpl;
import com.tfpower.arraydbs.beans.impl.BGraphProviderByRandomImpl;
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
        BiGraphProvider biGraphProvider = context.getBean(BGraphProviderByRandomImpl.class);
        List<BiGraph> testGraphs = biGraphProvider.getTestGraphs();
        ArrayJoiner arrayJoinerBase  = context.getBean(ArrayJoinerCacheEulerImpl.class);
        ArrayJoiner arrayJoinerRival = context.getBean(ArrayJoinerCacheHeuristicsImpl.class);
        for (BiGraph testGraph : testGraphs) {
            JoinReport joinReportBase  = arrayJoinerBase.join(testGraph);
            JoinReport joinReportRival = arrayJoinerRival.join(testGraph);
            logger.debug("Join base : {}", joinReportBase.toString());
            logger.debug("Join rival: {}", joinReportRival.toString());
            logger.info("Diff (csv) : {}", JoinReport.diff(joinReportBase, joinReportRival).toStringCsv(";"));

        }
    }

}
