package com.tfpower.arraydbs.beans;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by vlad on 21.02.18.
 */
@Component
public class BiGraphParseConfig {
    private String firstClassPrefix;
    private String secondClassPrefix;
    private Map<String, Object> additionalParams;

    public String getSecondClassPrefix() {
        return secondClassPrefix;
    }

    public void setSecondClassPrefix(String secondClassPrefix) {
        this.secondClassPrefix = secondClassPrefix;
    }

    public String getFirstClassPrefix() {
        return firstClassPrefix;
    }

    public void setFirstClassPrefix(String firstClassPrefix) {
        this.firstClassPrefix = firstClassPrefix;
    }

    public Map<String, Object> getAdditionalParams() {
        return additionalParams;
    }

    public void setAdditionalParams(Map<String, Object> additionalParams) {
        this.additionalParams = additionalParams;
    }
}
