package com.rollingstone.dto;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class StaffingSnapshot {

    public static class Row {
        private long unitId;
        private String unitCode;
        private long roleId;
        private String roleCode;
        private String shiftName; // DAY/EVE/NIGHT

        private int targetCount;
        private int minCount;
        private Integer maxCount;

        private int currentCount;
        private int variance; // current - target

        private boolean understaffed; // current < min
        private boolean overstaffed;  // current > target

        private List<Staffer> staff = new ArrayList<>();

        public long getUnitId() { return unitId; }
        public void setUnitId(long unitId) { this.unitId = unitId; }
        public String getUnitCode() { return unitCode; }
        public void setUnitCode(String unitCode) { this.unitCode = unitCode; }
        public long getRoleId() { return roleId; }
        public void setRoleId(long roleId) { this.roleId = roleId; }
        public String getRoleCode() { return roleCode; }
        public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
        public String getShiftName() { return shiftName; }
        public void setShiftName(String shiftName) { this.shiftName = shiftName; }
        public int getTargetCount() { return targetCount; }
        public void setTargetCount(int targetCount) { this.targetCount = targetCount; }
        public int getMinCount() { return minCount; }
        public void setMinCount(int minCount) { this.minCount = minCount; }
        public Integer getMaxCount() { return maxCount; }
        public void setMaxCount(Integer maxCount) { this.maxCount = maxCount; }
        public int getCurrentCount() { return currentCount; }
        public void setCurrentCount(int currentCount) { this.currentCount = currentCount; }
        public int getVariance() { return variance; }
        public void setVariance(int variance) { this.variance = variance; }
        public boolean isUnderstaffed() { return understaffed; }
        public void setUnderstaffed(boolean understaffed) { this.understaffed = understaffed; }
        public boolean isOverstaffed() { return overstaffed; }
        public void setOverstaffed(boolean overstaffed) { this.overstaffed = overstaffed; }
        public List<Staffer> getStaff() { return staff; }
        public void setStaff(List<Staffer> staff) { this.staff = staff; }
    }

    private String shiftName;
    private Instant windowStart;
    private Instant windowEnd;
    private List<Row> rows = new ArrayList<>();

    public String getShiftName() { return shiftName; }
    public void setShiftName(String shiftName) { this.shiftName = shiftName; }
    public Instant getWindowStart() { return windowStart; }
    public void setWindowStart(Instant windowStart) { this.windowStart = windowStart; }
    public Instant getWindowEnd() { return windowEnd; }
    public void setWindowEnd(Instant windowEnd) { this.windowEnd = windowEnd; }
    public List<Row> getRows() { return rows; }
    public void setRows(List<Row> rows) { this.rows = rows; }
}

