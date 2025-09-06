package com.rollingstone.staff;



import com.rollingstone.dto.Staffer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.*;

@Repository
public class StaffingJdbcRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final String schema;

    public StaffingJdbcRepository(NamedParameterJdbcTemplate jdbc,
                                  @Value("${app.schema:ai_agent_factory}") String schema) {
        this.jdbc = jdbc;
        this.schema = schema;
    }

    public List<Map<String,Object>> loadTargets(String shiftName, LocalDate effectiveDate) {
        String sql = """
            SELECT st.unit_id, u.code AS unit_code,
                   st.role_id, r.code AS role_code,
                   st.shift_name, st.target_count, st.min_count, st.max_count
            FROM %s.staffing_target st
            JOIN %s.unit u  ON u.unit_id = st.unit_id
            JOIN %s.role r  ON r.role_id = st.role_id
            WHERE st.shift_name = :shift
              AND st.effective_date = (
                 SELECT MAX(st2.effective_date)
                 FROM %s.staffing_target st2
                 WHERE st2.unit_id=st.unit_id AND st2.role_id=st.role_id
                   AND st2.shift_name=st.shift_name AND st2.effective_date <= :eff
              )
            """.formatted(schema,schema,schema,schema);

        return jdbc.queryForList(sql, Map.of("shift", shiftName, "eff", effectiveDate));
    }

    public Map<String,Integer> loadActualCounts(LocalDateTime start, LocalDateTime end) {
        String sql = """
            SELECT sa.unit_id, sa.role_id, COUNT(*) AS current_count
            FROM %s.staff_assignment sa
            WHERE sa.status IN ('ACTIVE','PLANNED')
              AND sa.start_dt < :end AND sa.end_dt > :start
            GROUP BY sa.unit_id, sa.role_id
            """.formatted(schema);

        Map<String,Integer> out = new HashMap<>();
        SqlParameterSource ps = new MapSqlParameterSource()
                .addValue("start", start)
                .addValue("end", end);
        jdbc.query(sql, ps, (ResultSet rs) -> {
            String key = rs.getLong("unit_id") + ":" + rs.getLong("role_id");
            out.put(key, rs.getInt("current_count"));
        });
        return out;
    }

    public Map<String,List<Staffer>> loadActualStaff(LocalDateTime start, LocalDateTime end) {
        String sql = """
            SELECT sa.unit_id, sa.role_id, s.staff_id, s.staff_no, s.first_name, s.last_name,
                   sa.start_dt, sa.end_dt, sa.status
            FROM %s.staff_assignment sa
            JOIN %s.staff s ON s.staff_id = sa.staff_id
            WHERE sa.status IN ('ACTIVE','PLANNED')
              AND sa.start_dt < :end AND sa.end_dt > :start
            """.formatted(schema, schema);

        Map<String,List<Staffer>> out = new HashMap<>();
        SqlParameterSource ps = new MapSqlParameterSource()
                .addValue("start", start).addValue("end", end);
        jdbc.query(sql, ps, (ResultSet rs) -> {
            String key = rs.getLong("unit_id") + ":" + rs.getLong("role_id");
            List<Staffer> list = out.computeIfAbsent(key, k -> new ArrayList<>());
            list.add(mapStaffer(rs));
        });
        return out;
    }

    private Staffer mapStaffer(ResultSet rs) throws SQLException {
        return new Staffer(
                rs.getLong("staff_id"),
                rs.getString("staff_no"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("status"),
                rs.getTimestamp("start_dt").toLocalDateTime(),
                rs.getTimestamp("end_dt").toLocalDateTime()
        );
    }

    /* ---------- Write ops for applyTransfer ---------- */

    public Map<String,Object> findActiveAssignment(long staffId, long roleId, long unitId,
                                                   LocalDateTime start, LocalDateTime end) {
        String sql = """
            SELECT * FROM %s.staff_assignment
            WHERE staff_id=:sid AND role_id=:rid AND unit_id=:uid
              AND status IN ('ACTIVE','PLANNED')
              AND start_dt < :end AND end_dt > :start
            ORDER BY start_dt DESC
            LIMIT 1
            """.formatted(schema);
        List<Map<String,Object>> rows = jdbc.queryForList(sql, Map.of(
                "sid", staffId, "rid", roleId, "uid", unitId,
                "start", start, "end", end
        ));
        return rows.isEmpty() ? null : rows.get(0);
    }

    public int closeAssignment(long assignmentId, LocalDateTime newEnd) {
        String sql = """
            UPDATE %s.staff_assignment
            SET end_dt=:newEnd, status='COMPLETED'
            WHERE assignment_id=:id AND start_dt < :newEnd AND end_dt > :newEnd
            """.formatted(schema);
        return jdbc.update(sql, Map.of("id", assignmentId, "newEnd", newEnd));
    }

    public long insertAssignment(long staffId, long roleId, long unitId,
                                 LocalDateTime start, LocalDateTime end,
                                 String status, String note) {
        String sql = """
            INSERT INTO %s.staff_assignment
              (staff_id, role_id, unit_id, start_dt, end_dt, status, note)
            VALUES(:sid,:rid,:uid,:start,:end,:status,:note)
            """.formatted(schema);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("sid", staffId)
                .addValue("rid", roleId)
                .addValue("uid", unitId)
                .addValue("start", start)
                .addValue("end", end)
                .addValue("status", status)
                .addValue("note", note), kh);
        Number key = kh.getKey();
        return key == null ? 0L : key.longValue();
    }
}
