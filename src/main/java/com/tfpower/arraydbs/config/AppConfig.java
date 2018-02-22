package com.tfpower.arraydbs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by vlad on 24.01.18.
 */
@Configuration
@ComponentScan(basePackages = "com.tfpower.arraydbs.beans")
@PropertySource("classpath:app.properties")
public class AppConfig {
//    @Bean
//    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
//        return new PropertySourcesPlaceholderConfigurer();
//    }
}
