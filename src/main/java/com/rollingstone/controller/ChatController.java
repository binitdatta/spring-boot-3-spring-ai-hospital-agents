package com.rollingstone.controller;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient orchestrator;

    public ChatController(ChatClient orchestrator) {
        this.orchestrator = orchestrator;
    }

    public record ChatReq(String input) {}

    @PostMapping(value = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> ask(@RequestBody ChatReq req,
                                   @RequestHeader(value="X-Role", required=false) String role) {
        var resp = orchestrator.prompt()
                .user(req.input())
                .call()
                .content();

        return Map.of(
                "role", role == null ? "guest" : role,
                "answer", resp
        );
    }
}
