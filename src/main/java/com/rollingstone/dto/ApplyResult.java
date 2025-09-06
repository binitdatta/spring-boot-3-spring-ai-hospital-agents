package com.rollingstone.dto;

import java.util.ArrayList;
import java.util.List;

public class ApplyResult {
    public static class Detail {
        private long staffId;
        private long fromUnitId;
        private long toUnitId;
        private Long closedAssignmentId;
        private Long newAssignmentId;
        private String status; // APPLIED/SKIPPED/ERROR
        private String message;

        public long getStaffId() { return staffId; }
        public void setStaffId(long staffId) { this.staffId = staffId; }
        public long getFromUnitId() { return fromUnitId; }
        public void setFromUnitId(long fromUnitId) { this.fromUnitId = fromUnitId; }
        public long getToUnitId() { return toUnitId; }
        public void setToUnitId(long toUnitId) { this.toUnitId = toUnitId; }
        public Long getClosedAssignmentId() { return closedAssignmentId; }
        public void setClosedAssignmentId(Long closedAssignmentId) { this.closedAssignmentId = closedAssignmentId; }
        public Long getNewAssignmentId() { return newAssignmentId; }
        public void setNewAssignmentId(Long newAssignmentId) { this.newAssignmentId = newAssignmentId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    private int appliedCount;
    private List<Detail> details = new ArrayList<>();

    public int getAppliedCount() { return appliedCount; }
    public void setAppliedCount(int appliedCount) { this.appliedCount = appliedCount; }
    public List<Detail> getDetails() { return details; }
    public void setDetails(List<Detail> details) { this.details = details; }
}

