package com.rollingstone.staff;



import com.rollingstone.dto.Staffer;
import com.rollingstone.dto.StaffingSnapshot;
import com.rollingstone.dto.TransferPlan;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransferPlannerService {

    public TransferPlan greedyBalance(StaffingSnapshot snap, Instant now) {
        // Partition by role
        Map<Long, List<StaffingSnapshot.Row>> byRole = snap.getRows().stream()
                .collect(Collectors.groupingBy(StaffingSnapshot.Row::getRoleId, LinkedHashMap::new, Collectors.toList()));

        List<TransferPlan.Transfer> moves = new ArrayList<>();
        for (var roleEntry : byRole.entrySet()) {
            long roleId = roleEntry.getKey();
            List<StaffingSnapshot.Row> rows = roleEntry.getValue();

            List<StaffingSnapshot.Row> over = rows.stream()
                    .filter(StaffingSnapshot.Row::isOverstaffed)
                    .sorted(Comparator.comparingInt(StaffingSnapshot.Row::getVariance).reversed())
                    .collect(Collectors.toList());


            List<StaffingSnapshot.Row> under = rows.stream()
                    .filter(StaffingSnapshot.Row::isUnderstaffed)
                    .sorted(Comparator.comparingInt((StaffingSnapshot.Row r) -> r.getMinCount() - r.getCurrentCount()).reversed())
                    .collect(Collectors.toList());

            for (StaffingSnapshot.Row deficit : under) {
                int needed = Math.max(deficit.getMinCount() - deficit.getCurrentCount(), 0);
                if (needed == 0) continue;

                Iterator<StaffingSnapshot.Row> it = over.iterator();
                while (needed > 0 && it.hasNext()) {
                    StaffingSnapshot.Row surplus = it.next();
                    int canGive = surplus.getCurrentCount() - surplus.getTargetCount();
                    if (canGive <= 0) continue;

                    int move = Math.min(needed, canGive);
                    // Pick specific staff: prefer PLANNED then ACTIVE
                    List<Staffer> candidates = new ArrayList<>();
                    candidates.addAll(surplus.getStaff().stream().filter(s -> "PLANNED".equalsIgnoreCase(s.getStatus())).toList());
                    candidates.addAll(surplus.getStaff().stream().filter(s -> "ACTIVE".equalsIgnoreCase(s.getStatus())).toList());

                    for (int i = 0; i < move && i < candidates.size(); i++) {
                        Staffer s = candidates.get(i);
                        if (surplus.getUnitId() == deficit.getUnitId()) continue;
                        TransferPlan.Transfer t = new TransferPlan.Transfer();
                        t.setStaffId(s.getStafferId());
                        t.setRoleId(roleId);
                        t.setFromUnitId(surplus.getUnitId());
                        t.setToUnitId(deficit.getUnitId());
                        t.setStart(now);
                        t.setEnd(snap.getWindowEnd());
                        t.setReason("Balance " + surplus.getUnitCode() + " → " + deficit.getUnitCode() +
                                " (role " + surplus.getRoleCode() + ")");
                        moves.add(t);
                    }

                    needed -= move;
                    surplus.setCurrentCount(surplus.getCurrentCount() - move);
                    deficit.setCurrentCount(deficit.getCurrentCount() + move);
                }
            }
        }

        TransferPlan plan = new TransferPlan();
        plan.setShiftName(snap.getShiftName());
        plan.setWindowStart(snap.getWindowStart());
        plan.setWindowEnd(snap.getWindowEnd());
        plan.setRequiresApproval(true);
        plan.setTransfers(moves);
        plan.setSummary("Proposed " + moves.size() + " transfers to satisfy mins and reduce variance.");
        return plan;
    }
}
