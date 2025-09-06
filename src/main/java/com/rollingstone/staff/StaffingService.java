package com.rollingstone.staff;



import com.rollingstone.dto.ShiftWindows;
import com.rollingstone.dto.StaffingSnapshot;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class StaffingService {

    private final StaffingJdbcRepository repo;

    public StaffingService(StaffingJdbcRepository repo) {
        this.repo = repo;
    }

    public StaffingSnapshot snapshot(String shiftName, Instant ts) {
        LocalDateTime[] win = ShiftWindows.window(shiftName, ts);
        LocalDate effDate = win[0].toLocalDate();

        var targets = repo.loadTargets(shiftName, effDate);
        var counts  = repo.loadActualCounts(win[0], win[1]);
        var staff   = repo.loadActualStaff(win[0], win[1]);

        Map<String,StaffingSnapshot.Row> byKey = new LinkedHashMap<>();
        for (var t : targets) {
            long unitId = ((Number)t.get("unit_id")).longValue();
            long roleId = ((Number)t.get("role_id")).longValue();
            String key = unitId + ":" + roleId;

            StaffingSnapshot.Row row = new StaffingSnapshot.Row();
            row.setUnitId(unitId);
            row.setUnitCode((String)t.get("unit_code"));
            row.setRoleId(roleId);
            row.setRoleCode((String)t.get("role_code"));
            row.setShiftName((String)t.get("shift_name"));
            row.setTargetCount(((Number)t.get("target_count")).intValue());
            row.setMinCount(((Number)t.get("min_count")).intValue());
            row.setMaxCount((t.get("max_count") == null) ? null : ((Number)t.get("max_count")).intValue());
            byKey.put(key, row);
        }

        // Fill actuals
        for (var e : counts.entrySet()) {
            StaffingSnapshot.Row row = byKey.computeIfAbsent(e.getKey(), k -> {
                String[] parts = k.split(":");
                StaffingSnapshot.Row r = new StaffingSnapshot.Row();
                r.setUnitId(Long.parseLong(parts[0]));
                r.setRoleId(Long.parseLong(parts[1]));
                r.setUnitCode("U" + parts[0]); // fallback
                r.setRoleCode("R" + parts[1]); // fallback
                r.setShiftName(shiftName);
                r.setTargetCount(0);
                r.setMinCount(0);
                r.setMaxCount(null);
                return r;
            });
            row.setCurrentCount(e.getValue());
        }

        // Attach staff lists + derive flags
        for (var entry : staff.entrySet()) {
            StaffingSnapshot.Row row = byKey.get(entry.getKey());
            if (row != null) row.setStaff(entry.getValue());
        }
        for (var row : byKey.values()) {
            row.setVariance(row.getCurrentCount() - row.getTargetCount());
            row.setUnderstaffed(row.getCurrentCount() < row.getMinCount());
            row.setOverstaffed(row.getCurrentCount() > row.getTargetCount());
        }

        StaffingSnapshot snap = new StaffingSnapshot();
        snap.setShiftName(shiftName);
        snap.setWindowStart(win[0].atZone(ZoneId.systemDefault()).toInstant());
        snap.setWindowEnd(win[1].atZone(ZoneId.systemDefault()).toInstant());
        snap.setRows(new ArrayList<>(byKey.values()));
        return snap;
    }
}
