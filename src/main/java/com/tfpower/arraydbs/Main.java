package com.tfpower.arraydbs;

import com.tfpower.arraydbs.beans.ArrayJoiner;
import com.tfpower.arraydbs.beans.BGraph;
import com.tfpower.arraydbs.config.AppConfig;
import com.tfpower.arraydbs.domain.JoinReport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by vlad on 24.01.18.
 */
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ArrayJoiner arrayJoiner = context.getBean(ArrayJoiner.class);
        BGraph bGraph = context.getBean(BGraph.class);
        JoinReport joinReport = arrayJoiner.join(bGraph);
        System.out.println("Over");
    }

}
