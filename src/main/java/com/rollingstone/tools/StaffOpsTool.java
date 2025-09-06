package com.rollingstone.tools;



import com.rollingstone.dto.ApplyResult;
import com.rollingstone.dto.ApplyTransferCmd;
import com.rollingstone.dto.StaffingSnapshot;
import com.rollingstone.dto.TransferPlan;
import com.rollingstone.nl.*;
import com.rollingstone.staff.StaffingService;
import com.rollingstone.staff.TransferApplyService;
import com.rollingstone.staff.TransferPlannerService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class StaffOpsTool {

    private final EnglishParser parser;
    private final StaffingService staffing;
    private final TransferPlannerService planner;
    private final TransferApplyService applier;

    public StaffOpsTool(EnglishParser parser,
                        StaffingService staffing,
                        TransferPlannerService planner,
                        TransferApplyService applier) {
        this.parser = parser;
        this.staffing = staffing;
        this.planner = planner;
        this.applier = applier;
    }

    @Tool(name = "staffOps", description = "Parse English staffing prompts and execute Snapshot/Plan/Apply.")
    public StaffOpsResponse staffOps(String prompt) {
        ParsedRequest pr = parser.parse(prompt);

        switch (pr.intent()) {
            case SNAPSHOT -> {
                String shift = pr.shiftName() != null ? pr.shiftName() : "DAY";
                Instant at = pr.at() != null ? pr.at() : Instant.now();
                StaffingSnapshot snap = staffing.snapshot(shift, at);
                return new StaffOpsResponse("SNAPSHOT", snap, null, null,
                        "Snapshot for shift=" + shift + " at=" + at);
            }
            case PLAN -> {
                String shift = pr.shiftName() != null ? pr.shiftName() : "DAY";
                Instant at = pr.at() != null ? pr.at() : Instant.now();
                var snap = staffing.snapshot(shift, at);
                TransferPlan plan = planner.greedyBalance(snap, at);
                return new StaffOpsResponse("PLAN", null, plan, null,
                        "Plan for shift=" + shift + " at=" + at);
            }
            case APPLY -> {
                // Build ApplyTransferCmd from parsed moves
                var moves = pr.moves().stream()
                        .map(m -> new ApplyTransferCmd.Move(
                                m.staffId(),
                                m.roleId(),
                                m.fromUnitId(),
                                m.toUnitId(),
                                m.splitFromAssignment(),
                                m.note()
                        ))
                        .toList();

                ApplyTransferCmd cmd = new ApplyTransferCmd(
                        pr.shiftName() != null ? pr.shiftName() : "DAY",
                        pr.start(),
                        pr.end(),
                        moves
                );
                ApplyResult res = applier.apply(cmd);
                return new StaffOpsResponse("APPLY", null, null, res,
                        "Applied " + res.getAppliedCount() + " move(s)");
            }
            case HELP, UNKNOWN -> {
                return new StaffOpsResponse("HELP", null, null, null,
                        "Try: “Show current vs target staffing for the DAY shift today 2–10 pm” " +
                                "or “Plan transfers to balance DAY shift” or “Move staff 2 from unit 2 to unit 1 role 3 for DAY shift (split)”.");
            }
        }
        return new StaffOpsResponse("HELP", null, null, null, "Unrecognized input.");
    }
}

