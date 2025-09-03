package com.insightfinder.gateway.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InsightFinderConfig {

    @Value("${insightfinder.url}")
    private String url;

    private static String staticUrl;

    @PostConstruct
    public void init() {
        staticUrl = url;
    }

    public static String getUrl() {
        return staticUrl;
    }
}