package com.insightfinder.gateway.service;

import com.insightfinder.gateway.config.InsightFinderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InsightFinderGatewayService {
    private final InsightFinderConfig ifconfig;

    @Autowired
    public InsightFinderGatewayService(InsightFinderConfig ifconfig) {
        this.ifconfig = ifconfig;
    }


    public String getModelConfigByOrder(String username, String licenseKey, int order){
        // Mock Json response
        return "{\n" +
                "  \"model\": \"gpt-3.5-turbo\",\n" +
                "  \"temperature\": 0.7,\n" +
                "  \"max_tokens\": 4000,\n" +
                "  \"top_p\": 1,\n" +
                "  \"frequency_penalty\": 0.0,\n" +
                "  \"presence_penalty\": 0.0\n" +
                "}";
    }

}
