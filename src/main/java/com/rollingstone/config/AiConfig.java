// src/main/java/com/rollingstone/config/AiConfig.java
package com.rollingstone.config;

import com.rollingstone.tools.BedTools;
import com.rollingstone.tools.EvsTools;
import com.rollingstone.tools.StaffTools;
import com.rollingstone.tools.StaffOpsTool;   // <-- add import
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;

@Configuration
public class AiConfig {

    @Bean
    ChatClient orchestrator(OpenAiChatModel model,
                            BedTools bedTools,
                            EvsTools evsTools,
                            StaffTools staffTools,
                            StaffOpsTool staffOpsTool,   // <-- inject it
                            PolicyInterceptor policy) {

        return ChatClient.builder(model)
                .defaultSystem("""
                    You are HospitalOps Orchestrator.
                    - Use tools; never free-text commit irreversible actions.
                    - For any clinical implications, propose and set requiresApproval=true.
                    - For staffing rebalancing, propose a PLAN first, then WRITE only when approved.
                    Respond in JSON for plans: {summary, steps[], requiresApproval}.
                """)
                // Register all tool beans here (order doesn’t matter)
                .defaultTools(bedTools, evsTools, staffTools, staffOpsTool)  // <-- include StaffOpsTool
                .defaultAdvisors(policy)
                .build();
    }
}
