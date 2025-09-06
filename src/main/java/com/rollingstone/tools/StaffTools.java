package com.rollingstone.tools;


import com.rollingstone.dto.ApplyResult;
import com.rollingstone.dto.ApplyTransferCmd;
import com.rollingstone.dto.StaffingSnapshot;
import com.rollingstone.dto.TransferPlan;
import com.rollingstone.staff.StaffingService;
import com.rollingstone.staff.TransferApplyService;
import com.rollingstone.staff.TransferPlannerService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class StaffTools {

    private final StaffingService staffingService;
    private final TransferPlannerService plannerService;
    private final TransferApplyService applyService;

    public StaffTools(StaffingService staffingService,
                      TransferPlannerService plannerService,
                      TransferApplyService applyService) {
        this.staffingService = staffingService;
        this.plannerService = plannerService;
        this.applyService = applyService;
    }

    // READ tool
    @Tool(name = "getStaffingSnapshot",
            description = "Return current vs target staffing per unit-role for a shiftName (DAY/EVE/NIGHT) and timestamp")
    public StaffingSnapshot getStaffingSnapshot(String shiftName, Instant ts) {
        return staffingService.snapshot(shiftName, ts == null ? Instant.now() : ts);
    }

    // PLAN tool
    @Tool(name = "planStaffTransfers",
            description = "Propose transfers to balance staffing (requiresApproval=true by default).")
    public TransferPlan planStaffTransfers(String shiftName, Instant ts) {
        StaffingSnapshot snap = getStaffingSnapshot(shiftName, ts);
        return plannerService.greedyBalance(snap, Instant.now());
    }

    // WRITE tool (guarded by PolicyInterceptor + hard guard)
    @Tool(name = "applyTransfer",
            description = "Apply a list of staff transfers by inserting/updating staff_assignment rows.")
    public ApplyResult applyTransfer(ApplyTransferCmd cmd) {
        return applyService.apply(cmd);
    }
}
