package com.insightfinder.gateway.utils;


import ch.qos.logback.core.joran.sanity.Pair;
import com.insightfinder.gateway.model.internal.InsightFinderAuthModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderUtil {
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    static Logger logger = LoggerFactory.getLogger(HeaderUtil.class);


    public static InsightFinderAuthModel extractAuthentication(String token) {

        if (token == null) {return null;}

        token = token.trim();
        if (token.isEmpty()) {
            logger.error("Empty token in Authorization header.");
            return null;
        }
        if (!token.startsWith(BEARER_TOKEN_PREFIX)) {
            logger.error("Invalid token format in Authorization header.");
            return null;
        }

        token = token.substring(BEARER_TOKEN_PREFIX.length());
        token = token.trim();
        if (token.isEmpty()) {
            logger.error("Empty token in Authorization header.");
            return null;
        }

        // Split the token by ':' to get the username and licenseKey
        if (!token.contains(":")) {
            logger.error("Invalid token format in Authorization header.");
            return null;
        }

        String[] tokens = token.split(":");
        return new InsightFinderAuthModel(tokens[0], tokens[1]);
    }
}
