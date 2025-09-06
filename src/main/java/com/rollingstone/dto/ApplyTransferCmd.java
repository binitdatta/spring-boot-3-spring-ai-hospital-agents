//package com.rollingstone.dto;
//
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ApplyTransferCmd {
//    private String shiftName;
//    private Instant windowStart;
//    private Instant windowEnd;
//    private List<TransferPlan.Transfer> transfers = new ArrayList<>();
//
//    public String getShiftName() { return shiftName; }
//    public void setShiftName(String shiftName) { this.shiftName = shiftName; }
//    public Instant getWindowStart() { return windowStart; }
//    public void setWindowStart(Instant windowStart) { this.windowStart = windowStart; }
//    public Instant getWindowEnd() { return windowEnd; }
//    public void setWindowEnd(Instant windowEnd) { this.windowEnd = windowEnd; }
//    public List<TransferPlan.Transfer> getTransfers() { return transfers; }
//    public void setTransfers(List<TransferPlan.Transfer> transfers) { this.transfers = transfers; }
//}

// src/main/java/com/rollingstone/dto/ApplyTransferCmd.java
package com.rollingstone.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record ApplyTransferCmd(
        @JsonProperty("shiftName") String shiftName,

        // ISO-8601 like "2025-08-25T14:00:00Z"
        @JsonProperty("effectiveStart")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant effectiveStart,

        @JsonProperty("effectiveEnd")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant effectiveEnd,

        @JsonProperty("moves") List<Move> moves
) {
    public static record Move(
            @JsonProperty("staffId") Long staffId,
            @JsonProperty("roleId") Long roleId,
            @JsonProperty("fromUnitId") Long fromUnitId,
            @JsonProperty("toUnitId") Long toUnitId,
            @JsonProperty("splitFromAssignment") Boolean splitFromAssignment,
            @JsonProperty("note") String note
    ) {}
}
