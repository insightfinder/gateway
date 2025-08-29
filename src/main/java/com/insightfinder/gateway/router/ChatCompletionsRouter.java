package com.insightfinder.gateway.router;

import com.insightfinder.gateway.controllers.ChatController;
import com.insightfinder.gateway.controllers.ChatStreamController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/v1/chat/completions")
public class ChatCompletionsRouter {

    private final ChatStreamController streamChatController;
    private final ChatController chatController;

    public ChatCompletionsRouter(ChatStreamController streamChatController, ChatController chatController) {
        this.streamChatController = streamChatController;
        this.chatController = chatController;
    }

    @PostMapping
    public Mono<ResponseEntity<?>> chatCompletions(
            @RequestBody Map<String, Object> requestBody) {

        // Check if streaming is requested
        Boolean isStream = (Boolean) requestBody.get("stream");
        if (Boolean.TRUE.equals(isStream)) {
            return streamChatController.chat(requestBody);
        } else {
            return chatController.chat(requestBody);
        }
    }

}
