package com.rollingstone.dto;



import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TransferPlan {

    public static class Transfer {
        private long staffId;
        private long roleId;
        private long fromUnitId;
        private long toUnitId;
        private Instant start;  // usually "now"
        private Instant end;    // usually windowEnd
        private String reason;

        public long getStaffId() { return staffId; }
        public void setStaffId(long staffId) { this.staffId = staffId; }
        public long getRoleId() { return roleId; }
        public void setRoleId(long roleId) { this.roleId = roleId; }
        public long getFromUnitId() { return fromUnitId; }
        public void setFromUnitId(long fromUnitId) { this.fromUnitId = fromUnitId; }
        public long getToUnitId() { return toUnitId; }
        public void setToUnitId(long toUnitId) { this.toUnitId = toUnitId; }
        public Instant getStart() { return start; }
        public void setStart(Instant start) { this.start = start; }
        public Instant getEnd() { return end; }
        public void setEnd(Instant end) { this.end = end; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    private String shiftName;
    private Instant windowStart;
    private Instant windowEnd;
    private boolean requiresApproval = true;
    private String summary;
    private List<Transfer> transfers = new ArrayList<>();

    public String getShiftName() { return shiftName; }
    public void setShiftName(String shiftName) { this.shiftName = shiftName; }
    public Instant getWindowStart() { return windowStart; }
    public void setWindowStart(Instant windowStart) { this.windowStart = windowStart; }
    public Instant getWindowEnd() { return windowEnd; }
    public void setWindowEnd(Instant windowEnd) { this.windowEnd = windowEnd; }
    public boolean isRequiresApproval() { return requiresApproval; }
    public void setRequiresApproval(boolean requiresApproval) { this.requiresApproval = requiresApproval; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<Transfer> getTransfers() { return transfers; }
    public void setTransfers(List<Transfer> transfers) { this.transfers = transfers; }
}
