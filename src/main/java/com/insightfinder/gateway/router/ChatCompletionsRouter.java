package com.insightfinder.gateway.router;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.util.Map;

@RestController
@RequestMapping("/v1/chat/completions")
public class ChatCompletionsRouter {

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
            .baseUrl("https://api.openai.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    @PostMapping
    public Mono<ResponseEntity<?>> chatCompletions(
            @RequestBody Map<String, Object> requestBody) {

        // Check if streaming is requested
        Boolean isStream = (Boolean) requestBody.get("stream");
        if (Boolean.TRUE.equals(isStream)) {
            return streamChatCompletions(requestBody);
        } else {
            return nonStreamChatCompletions(requestBody);
        }
    }

    private Mono<ResponseEntity<?>> nonStreamChatCompletions(Map<String, Object> requestBody) {
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

    private Mono<ResponseEntity<?>> streamChatCompletions(Map<String, Object> requestBody) {
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
