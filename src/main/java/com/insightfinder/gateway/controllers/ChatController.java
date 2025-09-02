package com.insightfinder.gateway.controllers;

import io.micrometer.observation.annotation.Observed;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Observed
public class ChatController {

    private final WebClient webClient;

    public ChatController() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @PostMapping("/chat/completions")
    @Observed(name = "chat.completions", contextualName = "chat-completions-request")
    public Mono<ResponseEntity<?>> chat(@RequestBody Map<String, Object> requestBody) {
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
