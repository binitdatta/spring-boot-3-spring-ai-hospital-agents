package com.rollingstone.composer;



import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;
import java.util.Map;

@Component
public class AnswerComposer {
    private final ChatClient noTools;

    public AnswerComposer(@Qualifier("chatNoTools") ChatClient noTools) {
        this.noTools = noTools;
    }

    public String compose(String question,
                          String sql,
                          Map<String,Object> params,
                          List<Map<String,Object>> rows,
                          String plannerNotes) {

        String dataPreview = rows.isEmpty() ? "(no rows)" :
                rows.subList(0, Math.min(rows.size(), 50)).toString(); // cap preview size

        var prompt = """
      You are a hospital operations analyst. Answer the user's question clearly and concisely.
      Use the SQL result table as the **source of truth**. If results are empty, say so and suggest a next step.
      Include key metrics and short reasoning. Do not include the raw SQL unless asked.
      If the planner made assumptions, reflect them.

      QUESTION:
      %s

      SQL (for transparency):
      %s

      PARAMS: %s
      PLANNER_NOTES: %s

      TOP ROWS (%d shown):
      %s
      """.formatted(question, sql, params, plannerNotes, Math.min(rows.size(), 50), dataPreview);

        return noTools.prompt().user(prompt).call().content();
    }
}
