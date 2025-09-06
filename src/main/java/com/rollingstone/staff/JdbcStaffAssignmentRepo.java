package com.rollingstone.staff;



import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository
public class JdbcStaffAssignmentRepo implements StaffAssignmentRepo {

    private final JdbcTemplate jdbc;

    public JdbcStaffAssignmentRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Long splitIfNeeded(Long staffId, Long roleId, Long fromUnitId, LocalDateTime start) {
        // Find overlapping ACTIVE/PLANNED assignment in the source unit
        String sel = """
            SELECT assignment_id
            FROM ai_agent_factory.staff_assignment
            WHERE staff_id=? AND role_id=? AND unit_id=?
              AND status IN ('PLANNED','ACTIVE')
              AND start_dt < ? AND end_dt > ?
            ORDER BY start_dt DESC
            LIMIT 1
            """;

        var rows = jdbc.query(sel,
                (rs, i) -> rs.getLong("assignment_id"),
                staffId, roleId, fromUnitId,
                Timestamp.valueOf(start), Timestamp.valueOf(start));

        if (rows.isEmpty()) return null;

        Long assignmentId = rows.get(0);

        // Close it at start - 1 second, and mark COMPLETED
        String upd = """
            UPDATE ai_agent_factory.staff_assignment
            SET end_dt = ?, status='COMPLETED'
            WHERE assignment_id = ?
            """;
        jdbc.update(upd, Timestamp.valueOf(start.minusSeconds(1)), assignmentId);

        return assignmentId;
    }

    @Override
    public Long insertAssignment(Long staffId, Long roleId, Long unitId,
                                 LocalDateTime start, LocalDateTime end,
                                 String status, String note) {
        String ins = """
            INSERT INTO ai_agent_factory.staff_assignment
              (staff_id, role_id, unit_id, start_dt, end_dt, status, note)
            VALUES (?,?,?,?,?,?,?)
            """;

        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(ins, new String[]{"assignment_id"});
            ps.setLong(1, staffId);
            ps.setLong(2, roleId);
            ps.setLong(3, unitId);
            ps.setTimestamp(4, Timestamp.valueOf(start));
            ps.setTimestamp(5, Timestamp.valueOf(end));
            ps.setString(6, status);
            ps.setString(7, note);
            return ps;
        }, kh);

        return (kh.getKey() == null) ? null : kh.getKey().longValue();
        // If your driver doesn’t return keys reliably, you can fall back to LAST_INSERT_ID():
        // return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }
}

