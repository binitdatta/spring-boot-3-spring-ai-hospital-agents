package com.rollingstone.config;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class PolicyInterceptor implements CallAdvisor, Ordered {

    private static final Set<String> WRITE_TOOLS = Set.of("dispatchEvsCleaning");

    @Override public String getName() { return "policy-interceptor"; }

    @Override public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        String role = (String) request.context().getOrDefault("X-Role", "guest");
        boolean allowed = role.equals("ChargeNurse") || role.equals("OpsManager");

        if (!allowed) {
            String policyText = """
                POLICY:
                - You may CALL write-tools only when `X-Role` is ChargeNurse or OpsManager.
                - Otherwise you must return a plan with requiresApproval=true and NOT invoke write-tools.
                - Write-tools include: %s.
                """.formatted(String.join(", ", WRITE_TOOLS));

            // Prepend a SystemMessage to the existing prompt
            List<Message> msgs = new ArrayList<>();
            msgs.add(new SystemMessage(policyText));
            msgs.addAll(request.prompt().getInstructions());  // existing messages

            // If you don't need to carry options/tools, this simple ctor is fine:
            Prompt newPrompt = new Prompt(msgs);

            ChatClientRequest newRequest = request.mutate()
                    .prompt(newPrompt)
                    .build();

            return chain.nextCall(newRequest);
        }

        return chain.nextCall(request);
    }
}
