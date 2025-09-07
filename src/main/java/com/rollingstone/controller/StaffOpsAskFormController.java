package com.rollingstone.controller;



import com.rollingstone.nl.StaffOpsResponse;
import com.rollingstone.tools.StaffOpsTool;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staffing/v2")
public class StaffOpsAskController {

    private final StaffOpsTool tool;

    public StaffOpsAskController(StaffOpsTool tool) {
        this.tool = tool;
    }

    public record AskPayload(String message) {}

    @PostMapping("/ask")
    public StaffOpsResponse ask(@RequestBody AskPayload payload) {
        return tool.staffOps(payload.message());
    }

    @PostMapping(value = "/ask/form", consumes = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public StaffOpsResponse askForm(
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "question", required = false) String question) {
        String text = (message != null && !message.isBlank()) ? message : question;
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Please provide a 'message' (or 'question') value.");
        }
        return tool.staffOps(text);
    }
}
