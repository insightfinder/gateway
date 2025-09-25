package com.insightfinder.gateway.model.internal;
import lombok.Data;

@Data
public class InsightFinderAuthModel {
    private String username;
    private String licenseKey;

    public InsightFinderAuthModel(String username, String licenseKey) {
        this.username = username;
        this.licenseKey = licenseKey;
    }
}
