package com.rollingstone.controller;



import com.rollingstone.tools.BedTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient-flow")
public class PatientFlowController {

    private final ChatClient orchestrator;

    public PatientFlowController(ChatClient orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/propose-bed")
    @SuppressWarnings("unchecked")
    public Map<String,Object> propose(@RequestBody Map<String,Object> body,
                                      @RequestHeader(value="X-Role", required=false) String role) {

        var req = new BedTools.BedAssignRequest(
                (String) body.getOrDefault("patientId","P123"),
                (String) body.getOrDefault("unit","MedSurg"),
                (List<String>) body.getOrDefault("constraints", List.of("isolation:none","sex:Any"))
        );

        var plan = orchestrator.prompt()
                .system("Plan a safe bed assignment using tools and return JSON with requiresApproval.")
                .user("""
              Patient %s requires a bed on unit %s. Constraints: %s.
              1) Propose a bed using `proposeBedAssignment`.
              2) Do NOT call write-tools; only propose and summarize.
              """.formatted(req.patientId(), req.unit(), String.join(",", req.constraints())))
                .call()
                .content();

        return Map.of("role", role == null ? "guest" : role, "plan", plan);
    }
}

