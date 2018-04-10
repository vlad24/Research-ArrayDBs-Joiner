package com.tfpower.arraydbs;

import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.BiGraphProvider;
import com.tfpower.arraydbs.beans.impl.*;
import com.tfpower.arraydbs.config.AppConfig;
import com.tfpower.arraydbs.entity.ExperimentConductor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * Created by vlad on 24.01.18.
 */
public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        final Class<? extends ArrayJoiner> baseJoinerClass = ArrayJoinerCacheEulerImpl.class;
        final List<Class<? extends ArrayJoiner>> rivalClasses = Arrays.asList(
                ArrayJoinerCacheHeuristicsMinFirstImpl.class,
                ArrayJoinerCacheHeuristicsMaxFirstImpl.class,
                ArrayJoinerCacheNaiveImpl.class
        );
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        BiGraphProvider testDataProvider = context.getBean(BiGraphProviderFromFileImpl.class);
        ArrayJoiner baseJoiner = context.getBean(baseJoinerClass);
        List<ArrayJoiner> rivalJoiners = rivalClasses.stream().map(context::getBean).collect(Collectors.toList());
        List<String> csvResult = ExperimentConductor.conductExperiments(baseJoiner, rivalJoiners, testDataProvider);
        logger.info("Program is over. CSV diffs:\n\n{}", csvResult.stream().collect(joining("\n")));
    }

}
