package com.rollingstone.tools;

import org.springframework.beans.factory.annotation.Qualifier;


import org.springframework.stereotype.Component;
import org.springframework.ai.chat.client.ChatClient;

import java.util.Map;

@Component
public class SqlPlanner {
    private final ChatClient noTools;
    private final String systemPrompt;

    public SqlPlanner(@Qualifier("chatNoTools") ChatClient orchestratorNoTools, // define a bean with no defaultTools
                      com.rollingstone.sql.SchemaProvider schema) {
        this.noTools = orchestratorNoTools;
        this.systemPrompt = """
                You are a hospital analytics SQL planner.
                ONLY produce **MySQL 8** compatible **read-only** SQL (SELECT ...).
                Use ONLY the tables/columns present in the provided schema.
                Use JOINs explicitly.
                Do NOT use any catalog/schema prefixes (no `db.table`). Use unqualified table names.
                Prefer WHERE filters and LIMIT.
                If the English question is ambiguous, assume sensible defaults and comment in "notes".

                Output STRICT JSON with NO markdown fences or commentary:
                {
                  "sql": "SELECT ... WHERE ... LIMIT 200",
                  "params": { "namedParam": "value", "minDate":"2025-08-01" },
                  "notes": "short reasoning or assumptions"
                }

                --- BEGIN SCHEMA ---
                %s
                --- END SCHEMA ---
                """.formatted(schema.schemaText());
    }

    public Map<String, Object> plan(String englishQuestion) {
        String json = noTools.prompt()
                .system(systemPrompt)
                .user("Question: " + englishQuestion + "\nReturn ONLY the JSON object described.")
                .call()
                .content();

        // Minimal parse (you can use Jackson)
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String,Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Planner did not return valid JSON:\n" + json, e);
        }
    }
}

