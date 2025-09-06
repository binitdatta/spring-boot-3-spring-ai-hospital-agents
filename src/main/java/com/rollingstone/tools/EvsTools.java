package com.rollingstone.tools;


import org.springframework.stereotype.Component;
import org.springframework.ai.tool.annotation.Tool;

import java.util.Map;

@Component
public class EvsTools {

    public record EvsRequest(String roomId, String priority) {}
    public record EvsTicket(String ticketId, String status, String etaMinutes) {}

    @Tool(name = "dispatchEvsCleaning",
            description = "Create a cleaning ticket for a room; returns ticket id and ETA.")
    public EvsTicket dispatch(EvsRequest req) {
        // Demo: would call EVS system; we simulate
        String tid = "EVS-%d".formatted(Math.abs(req.hashCode()) % 10000);
        String eta = switch (req.priority() == null ? "normal" : req.priority()) {
            case "stat" -> "10";
            case "high" -> "20";
            default -> "45";
        };
        return new EvsTicket(tid, "QUEUED", eta);
    }
}

