package com.rollingstone.controller;



import com.rollingstone.composer.AnswerComposer;
import com.rollingstone.tools.SqlExecutorTool;
import com.rollingstone.tools.SqlPlanner;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/sqlqa")
public class SqlQaController {

    private final SqlPlanner planner;
    private final SqlExecutorTool executor;
    private final AnswerComposer composer;

    public SqlQaController(SqlPlanner planner, SqlExecutorTool executor, AnswerComposer composer) {
        this.planner = planner;
        this.executor = executor;
        this.composer = composer;
    }

    public record AskReq(String question, Integer limit) {}
    public record AskResp(String sql, Map<String,Object> params, Integer rowCount, String answer) {}

    @PostMapping("/ask")
    public AskResp ask(@RequestBody AskReq req) {
        Map<String, Object> plan = planner.plan(req.question());
        String sql = String.valueOf(plan.getOrDefault("sql", ""));
        @SuppressWarnings("unchecked")
        Map<String,Object> params = (Map<String,Object>) plan.getOrDefault("params", Map.of());
        String notes = String.valueOf(plan.getOrDefault("notes", ""));

        var result = executor.runReadOnly(sql, params, req.limit() == null ? 200 : req.limit());
        String answer = composer.compose(req.question(), result.sql(), result.params(), result.rows(), notes);

        return new AskResp(result.sql(), result.params(), result.rowCount(), answer);
    }
}

