package com.rollingstone.nl;



import java.time.Instant;
import java.util.List;

public record ParsedRequest(
        NlIntent intent,
        String shiftName,            // e.g. "DAY" | "EVE" | "NIGHT"
        Instant at,                  // “as of” timestamp (snapshot/plan)
        Instant start,               // window start (apply or explicit window)
        Instant end,                 // window end
        List<ParsedMove> moves,      // for APPLY
        String raw                   // original user text (for audit/debug)
) {
    public static ParsedRequest empty(NlIntent intent, String raw) {
        return new ParsedRequest(intent, null, null, null, null, List.of(), raw);
    }
}

