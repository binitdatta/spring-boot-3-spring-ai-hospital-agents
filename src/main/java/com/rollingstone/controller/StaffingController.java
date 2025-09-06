package com.rollingstone.controller;


import com.rollingstone.dto.ApplyResult;
import com.rollingstone.dto.ApplyTransferCmd;
import com.rollingstone.dto.StaffingSnapshot;
import com.rollingstone.dto.TransferPlan;
import com.rollingstone.staff.StaffingService;
import com.rollingstone.staff.TransferApplyService;
import com.rollingstone.staff.TransferPlannerService;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/staffing/v1")
public class StaffingController {

    private final StaffingService staffingService;
    private final TransferPlannerService plannerService;
    private final TransferApplyService applyService;

    public StaffingController(StaffingService staffingService,
                              TransferPlannerService plannerService,
                              TransferApplyService applyService) {
        this.staffingService = staffingService;
        this.plannerService = plannerService;
        this.applyService = applyService;
    }

    @GetMapping("/snapshot")
    public StaffingSnapshot snapshot(@RequestParam String shift,
                                     @RequestParam(required = false) String ts) {
        return staffingService.snapshot(shift, ts == null ? Instant.now() : Instant.parse(ts));
    }

    @GetMapping("/plan")
    public TransferPlan plan(@RequestParam String shift,
                             @RequestParam(required = false) String ts) {
        var snap = staffingService.snapshot(shift, ts == null ? Instant.now() : Instant.parse(ts));
        return plannerService.greedyBalance(snap, Instant.now());
    }

    @PostMapping("/apply")
    public ApplyResult apply(@RequestBody ApplyTransferCmd cmd) {
        return applyService.apply(cmd);
    }
}
