package com.rollingstone.nl;



import com.rollingstone.dto.ApplyResult;
import com.rollingstone.dto.StaffingSnapshot;
import com.rollingstone.dto.TransferPlan;

public record StaffOpsResponse(
        String action,                  // "SNAPSHOT" | "PLAN" | "APPLY" | "HELP"
        StaffingSnapshot snapshot,      // nullable
        TransferPlan plan,              // nullable
        ApplyResult apply,              // nullable
        String note                     // any hints/messages
) {}

