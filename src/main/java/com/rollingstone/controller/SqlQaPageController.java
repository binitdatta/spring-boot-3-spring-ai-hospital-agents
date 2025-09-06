package com.rollingstone.controller;

import com.rollingstone.composer.AnswerComposer;
import com.rollingstone.tools.SqlExecutorTool;
import com.rollingstone.tools.SqlPlanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class SqlQaPageController {

    private final SqlPlanner planner;
    private final SqlExecutorTool executor;
    private final AnswerComposer composer;
    private final ObjectMapper om = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public SqlQaPageController(SqlPlanner planner, SqlExecutorTool executor, AnswerComposer composer) {
        this.planner = planner;
        this.executor = executor;
        this.composer = composer;
    }

    @GetMapping({"/", "/sqlqa"})
    public String form(Model model,
                       @RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "limit", required = false) Integer limit) {
        String defaultQ = """
List OUT_OF_SERVICE beds with unit.code, room_number, bed_label, and days_since_last_cleaned = TIMESTAMPDIFF(DAY, bed.last_cleaned_at, NOW()). Use bed -> room -> unit. Order by days_since_last_cleaned DESC.
        """.trim();
        model.addAttribute("question", q == null ? defaultQ : q);
        model.addAttribute("limit", limit == null ? 500 : limit);
        model.addAttribute("ran", false);
        return "sqlqa";
    }

    @PostMapping("/sqlqa/run")
    public String run(@RequestParam("question") String question,
                      @RequestParam("limit") Integer limit,
                      Model model) throws Exception {

        // 1) Plan
        Map<String, Object> plan = planner.plan(question);
        String sql = String.valueOf(plan.getOrDefault("sql", "")).trim();
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) plan.getOrDefault("params", Map.of());
        String notes = String.valueOf(plan.getOrDefault("notes", ""));

        // 2) Execute (read-only)
        var result = executor.runReadOnly(sql, params, (limit == null ? 200 : limit));

        // 3) Compose final answer
        String answer = composer.compose(question, result.sql(), result.params(), result.rows(), notes);

        // 4) Prepare view model
        String paramsJson = om.writeValueAsString(result.params());

        // Build a stable list of columns from the first row (normalize keys to String)
        List<String> cols = result.rows().isEmpty()
                ? List.of()
                : result.rows().get(0).keySet().stream()
                .map(Object::toString)
                .toList();

        // Optional: useful to debug if the grid ever looks empty
        String rowsJson = om.writeValueAsString(result.rows());

        model.addAttribute("ran", true);
        model.addAttribute("question", question);
        model.addAttribute("limit", limit);
        model.addAttribute("sql", result.sql());
        model.addAttribute("paramsJson", paramsJson);
        model.addAttribute("rowCount", result.rowCount());
        model.addAttribute("elapsed", result.ms());
        model.addAttribute("answer", answer);
        model.addAttribute("rows", result.rows());   // List<Map<String,Object>>
        model.addAttribute("cols", cols);            // List<String>
        model.addAttribute("rowsJson", rowsJson);    // for optional debug
        return "sqlqa";
    }
}
