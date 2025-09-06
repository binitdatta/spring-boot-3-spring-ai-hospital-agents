package com.rollingstone.staff;



import java.time.LocalDateTime;

public interface StaffAssignmentRepo {

    /**
     * If there is an ACTIVE/PLANNED assignment for this staff/role/unit
     * overlapping the given start time, close it at start-1s and return its id,
     * else return null.
     */
    Long splitIfNeeded(Long staffId, Long roleId, Long fromUnitId, LocalDateTime start);

    /**
     * Insert a new assignment row and return the generated assignment_id.
     */
    Long insertAssignment(Long staffId,
                          Long roleId,
                          Long unitId,
                          LocalDateTime start,
                          LocalDateTime end,
                          String status,
                          String note);
}

