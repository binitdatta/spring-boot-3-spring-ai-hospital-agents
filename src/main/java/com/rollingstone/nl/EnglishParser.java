package com.rollingstone.nl;



import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

@Component
public class EnglishParser {

    private static final Pattern SHIFT =
            Pattern.compile("\\b(DAY|EVE|EVENING|NIGHT)\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern TIME_RANGE =
            Pattern.compile("(?:from\\s+)?(\\d{1,2}:?\\d{0,2}\\s*(?:am|pm)?)\\s*(?:to|–|-|—)\\s*(\\d{1,2}:?\\d{0,2}\\s*(?:am|pm)?)",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern ISO_START =
            Pattern.compile("start(?:\\s*at|:)\\s*(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:?\\d{0,2}Z?)", Pattern.CASE_INSENSITIVE);

    private static final Pattern ISO_END =
            Pattern.compile("end(?:\\s*at|:)\\s*(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:?\\d{0,2}Z?)", Pattern.CASE_INSENSITIVE);

    private static final Pattern MOVE_MINIMAL =
            Pattern.compile("move\\s+staff\\s*(\\d+)\\s+from\\s*unit\\s*(\\d+)\\s+to\\s*unit\\s*(\\d+)(?:.*?role\\s*(\\d+))?",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern APPLY_KEYWORDS =
            Pattern.compile("\\b(transfer|move|apply)\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern PLAN_KEYWORDS =
            Pattern.compile("\\b(plan|balance|rebalance|propose)\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern SNAPSHOT_KEYWORDS =
            Pattern.compile("\\b(snapshot|show|display|current(\\s+vs\\s+target)?)\\b", Pattern.CASE_INSENSITIVE);

    private final ZoneId zone = ZoneId.systemDefault();

    public ParsedRequest parse(String raw) {
        String text = Optional.ofNullable(raw).orElse("").trim();
        if (text.isEmpty()) return ParsedRequest.empty(NlIntent.HELP, raw);

        // 1) intent
        NlIntent intent = pickIntent(text);

        // 2) shift
        String shiftName = extractShift(text);

        // 3) time window
        Instant now = Instant.now();
        Instant start = null, end = null, at = null;

        // ISO window?
        start = extractIso(ISO_START, text).orElse(null);
        end   = extractIso(ISO_END, text).orElse(null);

        // natural “today 2pm–10pm”?
        if (start == null || end == null) {
            var tr = extractTimeRangeToday(text);
            if (tr != null) { start = tr[0]; end = tr[1]; }
        }

        // if snapshot/plan, set an “as of” instant
        if (intent == NlIntent.SNAPSHOT || intent == NlIntent.PLAN) {
            at = now;
        }

        // 4) moves (for APPLY)
        List<ParsedMove> moves = Collections.emptyList();
        if (intent == NlIntent.APPLY) {
            moves = extractMoves(text);
            // default window if not supplied
            if (start == null || end == null) {
                // Use shift if provided, otherwise 8h window from now
                if (shiftName != null && shiftName.equalsIgnoreCase("DAY")) {
                    start = todayAt(14, 0); // 2pm local default
                    end   = todayAt(22, 0); // 10pm
                } else {
                    start = now;
                    end   = now.plus(Duration.ofHours(8));
                }
            }
        }

        return new ParsedRequest(intent, shiftName, at, start, end, moves, raw);
    }

    private NlIntent pickIntent(String text) {
        if (APPLY_KEYWORDS.matcher(text).find()) return NlIntent.APPLY;
        if (PLAN_KEYWORDS.matcher(text).find())  return NlIntent.PLAN;
        if (SNAPSHOT_KEYWORDS.matcher(text).find()) return NlIntent.SNAPSHOT;

        // common phrasings
        if (text.toLowerCase().contains("show") || text.toLowerCase().contains("how many"))
            return NlIntent.SNAPSHOT;

        return NlIntent.UNKNOWN;
    }

    private String extractShift(String text) {
        var m = SHIFT.matcher(text);
        if (m.find()) {
            String s = m.group(1).toUpperCase(Locale.ROOT);
            return switch (s) {
                case "EVENING" -> "EVE";
                default -> s; // DAY/EVE/NIGHT
            };
        }
        return null;
    }

    private Optional<Instant> extractIso(Pattern p, String text) {
        var m = p.matcher(text);
        if (m.find()) {
            String iso = m.group(1);
            // trust ISO 8601 if present
            try {
                if (!iso.endsWith("Z") && iso.length() == 19) iso = iso + "Z"; // naive → Z
                return Optional.of(Instant.parse(iso));
            } catch (Exception ignored) {}
        }
        return Optional.empty();
    }

    private Instant[] extractTimeRangeToday(String text) {
        var m = TIME_RANGE.matcher(text);
        if (!m.find()) return null;
        try {
            LocalDate today = LocalDate.now(zone);
            var start = parseLocalTime(m.group(1));
            var end   = parseLocalTime(m.group(2));
            return new Instant[]{
                    ZonedDateTime.of(today, start, zone).toInstant(),
                    ZonedDateTime.of(today, end, zone).toInstant()
            };
        } catch (Exception ignored) {
            return null;
        }
    }

    private LocalTime parseLocalTime(String s) {
        s = s.trim().toUpperCase(Locale.ROOT)
                .replaceAll("\\s+", "");
        // allow “2pm”, “2:00pm”, “14:00”
        DateTimeFormatter[] fmts = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("h:mma"),
                DateTimeFormatter.ofPattern("ha"),
                DateTimeFormatter.ofPattern("H:mm"),
                DateTimeFormatter.ofPattern("H")
        };
        for (var f : fmts) {
            try { return LocalTime.parse(s, f); } catch (Exception ignored) {}
        }
        // fallback: “2” => 2:00
        int hour = Integer.parseInt(s.replaceAll("\\D", ""));
        return LocalTime.of(hour, 0);
    }

    private List<ParsedMove> extractMoves(String text) {
        List<ParsedMove> moves = new ArrayList<>();
        var m = MOVE_MINIMAL.matcher(text);
        while (m.find()) {
            Long staffId = Long.parseLong(m.group(1));
            Long fromU   = Long.parseLong(m.group(2));
            Long toU     = Long.parseLong(m.group(3));
            Long roleId  = (m.group(4) != null) ? Long.parseLong(m.group(4)) : null;

            boolean split = text.toLowerCase().contains("split");
            String note = "NL move";
            moves.add(new ParsedMove(staffId, roleId, fromU, toU, split, note));
        }
        return moves;
    }

    private Instant todayAt(int h, int m) {
        return ZonedDateTime.of(LocalDate.now(zone), LocalTime.of(h, m), zone).toInstant();
    }
}

