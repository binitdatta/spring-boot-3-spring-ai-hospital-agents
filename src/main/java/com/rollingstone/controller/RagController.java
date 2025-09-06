package com.rollingstone.controller;


import com.rollingstone.rag.RagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService rag;

    public RagController(RagService rag) {
        this.rag = rag;
    }

    @PostMapping("/ingest")
    public Map<String,Object> ingest() throws Exception {
        int n1 = rag.ingestClasspathMd("/docs/bed-management-sop.md", "bed-sop");
        int n2 = rag.ingestClasspathMd("/docs/discharge-checklist.md", "discharge");
        return Map.of("ingested", n1+n2, "details", Map.of("bed", n1, "discharge", n2));
    }

    public record SearchReq(String q, Integer k) {}

    @PostMapping("/search")
    public List<Map<String,Object>> search(@RequestBody SearchReq req) {
        return rag.search(req.q(), req.k()==null?5:req.k());
    }
}
