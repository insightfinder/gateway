package com.insightfinder.gateway.router;

import com.insightfinder.gateway.controller.ChatController;
import com.insightfinder.gateway.controller.ChatStreamController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.insightfinder.gateway.utils.HeaderUtil;

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
    public Mono<ResponseEntity<?>> chatCompletions(@RequestHeader("Authorization") String authorizationHeader,
                                                   @RequestBody Map<String, Object> requestBody) {


        // Extract the InsightFinder username / licenseKey from the authorization header
        var ifAuthentication = HeaderUtil.extractAuthentication(authorizationHeader);
        if (ifAuthentication==null){
            // Return 401 Unauthorized if the authorization header is invalid
            return Mono.just(ResponseEntity.badRequest().build());
        }

        // Check if streaming is requested
        Boolean isStream = (Boolean) requestBody.get("stream");
        if (Boolean.TRUE.equals(isStream)) {
            return streamChatController.chatWithGateway(requestBody,ifAuthentication);
        } else {
            return chatController.chatWithGateway(requestBody, ifAuthentication);
        }
    }
}
