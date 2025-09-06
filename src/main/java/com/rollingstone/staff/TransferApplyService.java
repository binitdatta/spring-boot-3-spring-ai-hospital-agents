//package com.rollingstone.staff;
//
//
//import com.rollingstone.dto.ApplyResult;
//import com.rollingstone.dto.ApplyTransferCmd;
//import com.rollingstone.dto.TransferPlan;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.*;
//import java.util.Map;
//
//@Service
//public class TransferApplyService {
//
//    private final StaffingJdbcRepository repo;
//    private final HttpServletRequest http; // to inspect headers for governance
//
//    public TransferApplyService(StaffingJdbcRepository repo, HttpServletRequest http) {
//        this.repo = repo; this.http = http;
//    }
//
//    /** Extra hard guard in addition to your PolicyInterceptor prompt guard. */
//    private void assertRoleAllowed() {
//        String role = http.getHeader("X-Role");
//        boolean allowed = "ChargeNurse".equals(role) || "OpsManager".equals(role);
//        if (!allowed) {
//            throw new SecurityException("applyTransfer requires X-Role=ChargeNurse or OpsManager");
//        }
//    }
//
//    @Transactional
//    public ApplyResult apply(ApplyTransferCmd cmd) {
//        assertRoleAllowed();
//
//        ApplyResult result = new ApplyResult();
//        int applied = 0;
//
//        LocalDateTime winStart = LocalDateTime.ofInstant(cmd.getWindowStart(), ZoneId.systemDefault());
//        LocalDateTime winEnd   = LocalDateTime.ofInstant(cmd.getWindowEnd(), ZoneId.systemDefault());
//        LocalDateTime now      = LocalDateTime.now();
//
//        for (TransferPlan.Transfer t : cmd.getTransfers()) {
//            ApplyResult.Detail det = new ApplyResult.Detail();
//            det.setStaffId(t.getStaffId());
//            det.setFromUnitId(t.getFromUnitId());
//            det.setToUnitId(t.getToUnitId());
//            try {
//                Map<String,Object> a = repo.findActiveAssignment(
//                        t.getStaffId(), t.getRoleId(), t.getFromUnitId(), winStart, winEnd);
//
//                if (a != null) {
//                    long assignmentId = ((Number)a.get("assignment_id")).longValue();
//                    // Close the current assignment at 'now'
//                    int closed = repo.closeAssignment(assignmentId, now);
//                    if (closed > 0) det.setClosedAssignmentId(assignmentId);
//                    // Insert a new assignment in the destination for the remainder of the window
//                    long newId = repo.insertAssignment(
//                            t.getStaffId(), t.getRoleId(), t.getToUnitId(),
//                            now, winEnd, "ACTIVE", "Transfer: " + t.getReason());
//                    det.setNewAssignmentId(newId);
//                    det.setStatus("APPLIED");
//                    det.setMessage("Split & moved to dest unit.");
//                    applied++;
//                } else {
//                    // No overlapping current assignment; add a PLANNED one for the window remainder
//                    long newId = repo.insertAssignment(
//                            t.getStaffId(), t.getRoleId(), t.getToUnitId(),
//                            now, winEnd, "PLANNED", "Planned transfer: " + t.getReason());
//                    det.setNewAssignmentId(newId);
//                    det.setStatus("APPLIED");
//                    det.setMessage("No source row; created PLANNED at destination.");
//                    applied++;
//                }
//
//            } catch (Exception e) {
//                det.setStatus("ERROR");
//                det.setMessage(e.getClass().getSimpleName()+": "+e.getMessage());
//            }
//            result.getDetails().add(det);
//        }
//        result.setAppliedCount(applied);
//        return result;
//    }
//}

// src/main/java/com/rollingstone/staff/TransferApplyService.java
package com.rollingstone.staff;

import com.rollingstone.dto.ApplyResult;
import com.rollingstone.dto.ApplyTransferCmd;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Objects;

@Service
public class TransferApplyService {

    private final ZoneId zone = ZoneId.systemDefault();
    private final StaffAssignmentRepo repo;

    public TransferApplyService(StaffAssignmentRepo repo) {
        this.repo = repo;
    }

    @Transactional
    public ApplyResult apply(ApplyTransferCmd cmd) {
        // Default/fallback times so we never pass a null Instant
        Instant startInstant = Objects.requireNonNullElse(cmd.effectiveStart(), Instant.now());
        Instant endInstant   = Objects.requireNonNullElseGet(cmd.effectiveEnd(),
                () -> startInstant.plus(Duration.ofHours(8)));

        var start = LocalDateTime.ofInstant(startInstant, zone);
        var end   = LocalDateTime.ofInstant(endInstant, zone);

        if (cmd.moves() == null || cmd.moves().isEmpty()) {
            throw new IllegalArgumentException("No moves supplied.");
        }

        ApplyResult out = new ApplyResult();
        int applied = 0;

        for (var m : cmd.moves()) {
            ApplyResult.Detail d = new ApplyResult.Detail();
            d.setStaffId(m.staffId());
            d.setFromUnitId(m.fromUnitId());
            d.setToUnitId(m.toUnitId());
            d.setStatus("SKIPPED"); // default until we succeed

            try {
                Long closedId = null;
                if (Boolean.TRUE.equals(m.splitFromAssignment())) {
                    closedId = repo.splitIfNeeded(m.staffId(), m.roleId(), m.fromUnitId(), start);
                    d.setClosedAssignmentId(closedId);
                }

                Long newId = repo.insertAssignment(
                        m.staffId(), m.roleId(), m.toUnitId(),
                        start, end,
                        "ACTIVE",
                        m.note()
                );
                d.setNewAssignmentId(newId);
                d.setStatus("APPLIED");
                d.setMessage("Transfer applied for staff " + m.staffId());
                applied++;

            } catch (Exception ex) {
                d.setStatus("ERROR");
                d.setMessage(ex.getMessage());
            }

            out.getDetails().add(d);
        }

        out.setAppliedCount(applied);
        return out;
    }
}
