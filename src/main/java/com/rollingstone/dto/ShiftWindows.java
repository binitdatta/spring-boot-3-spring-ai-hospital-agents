package com.rollingstone.dto;

import java.time.*;

public final class ShiftWindows {
    private ShiftWindows() {}

    /** Returns [start,end) LocalDateTime for a shift on the date/time of ts (local server TZ). */
    public static LocalDateTime[] window(String shiftName, Instant ts) {
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime ldt = LocalDateTime.ofInstant(ts, zone);
        LocalDate d = ldt.toLocalDate();
        switch (shiftName == null ? "" : shiftName.toUpperCase()) {
            case "DAY":   return new LocalDateTime[]{ d.atTime(7,0),  d.atTime(15,0) };
            case "EVE":   return new LocalDateTime[]{ d.atTime(15,0), d.atTime(23,0) };
            case "NIGHT": return new LocalDateTime[]{ d.atTime(23,0), d.plusDays(1).atTime(7,0) };
            default:      return new LocalDateTime[]{ d.atStartOfDay(), d.plusDays(1).atStartOfDay() };
        }
    }
}

