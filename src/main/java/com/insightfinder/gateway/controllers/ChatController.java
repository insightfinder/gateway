package com.insightfinder.gateway.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
public class ChatController {

    private final WebClient webClient;

    public ChatController() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<ResponseEntity<?>> chat(Map<String, Object> requestBody) {
        return webClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(requestBody)
                .header("Authorization", "Bearer ")
                .retrieve()
                .toEntity(Map.class)
                .map(response -> ResponseEntity.status(response.getStatusCode())
                        .headers(response.getHeaders())
                        .body((Map<String, Object>) response.getBody()));
    }
}
