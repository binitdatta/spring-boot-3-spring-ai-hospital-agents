package com.rollingstone.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.chat.client.ChatClient;

@Configuration
public class AiNoToolsConfig {

    @Bean("chatNoTools")
    ChatClient chatNoTools(OpenAiChatModel model) {
        return ChatClient.builder(model)
                .defaultSystem("""
        You are a precise, deterministic assistant for hospital analytics.
        Prefer short, accurate answers, include numbers and units.
      """)
                .build();
    }
}
