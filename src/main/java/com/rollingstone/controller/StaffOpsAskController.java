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

}
