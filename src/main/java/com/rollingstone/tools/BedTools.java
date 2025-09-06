package com.rollingstone.tools;


import org.springframework.stereotype.Component;
import org.springframework.ai.tool.annotation.Tool;
import jakarta.validation.constraints.NotBlank;

import java.util.*;

@Component
public class BedTools {

    public record BedAssignRequest(
            @NotBlank String patientId,
            @NotBlank String unit,
            List<String> constraints
    ) {}

    public record BedAssignment(
            String bedId, String reason, boolean requiresApproval
    ) {}

    @Tool(name = "proposeBedAssignment",
            description = "Propose a specific bed for a patient with constraints; does not write to EHR.")
    public BedAssignment propose(BedAssignRequest req) {
        // Demo logic: in reality call bed board / rules engine.
        String bed = switch (req.unit()) {
            case "CCU" -> "CCU-12";
            case "MedSurg" -> "MS-233";
            default -> "GEN-101";
        };
        String why = "Matches unit=%s; constraints=%s; bed is clean and available."
                .formatted(req.unit(), req.constraints());
        return new BedAssignment(bed, why, true); // require nurse approval
    }
}

