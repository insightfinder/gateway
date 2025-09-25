package com.insightfinder.gateway.controller;

import com.insightfinder.gateway.model.internal.InsightFinderAuthModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
public class ChatStreamController {

    private final WebClient webClient;

    ChatStreamController() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<ResponseEntity<?>> chatWithGateway(Map<String, Object> requestBody, InsightFinderAuthModel ifAuthentication) {
        Flux<ServerSentEvent<String>> eventStream = webClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(requestBody)
                .header("Authorization", "Bearer ")
                .retrieve()
                .bodyToFlux(String.class)
                .map(chunk -> {
                    // Handle the streaming chunks from OpenAI
                    if (chunk.startsWith("data: ")) {
                        return ServerSentEvent.builder(chunk.substring(6)).build();
                    } else {
                        return ServerSentEvent.builder(chunk).build();
                    }
                });

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(eventStream));
    }
}
