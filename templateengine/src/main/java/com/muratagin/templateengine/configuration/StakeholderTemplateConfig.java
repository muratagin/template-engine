package com.muratagin.templateengine.configuration;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class StakeholderTemplateConfig {

    private String targetEndpoint;
    private String targetRequestType;
    private Map<String, String> bodyMapping;
    private Map<String, String> headerMapping;
}
