package com.rollingstone.controller;



import com.rollingstone.nl.StaffOpsResponse;
import com.rollingstone.tools.StaffOpsTool;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/staffing/v3")
public class StaffOpsAskFormController {

    private final StaffOpsTool tool;

    public StaffOpsAskFormController(StaffOpsTool tool) {
        this.tool = tool;
    }

    public record AskPayload(String message) {}



//    @PostMapping(value = "/ask/form", consumes = org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public StaffOpsResponse askForm(
//            @RequestParam(value = "message", required = false) String message,
//            @RequestParam(value = "question", required = false) String question) {
//        String text = (message != null && !message.isBlank()) ? message : question;
//        if (text == null || text.isBlank()) {
//            throw new IllegalArgumentException("Please provide a 'message' (or 'question') value.");
//        }
//        return tool.staffOps(text);
//    }

    // Handle form submit (POST x-www-form-urlencoded)
    @PostMapping(value = "/ask/form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String askForm(@RequestParam(value = "message", required = false) String message,
                          @RequestParam(value = "question", required = false) String question,
                          Model model) {

        String text = (message != null && !message.isBlank()) ? message : question;
        if (text == null || text.isBlank()) {
            model.addAttribute("error", "Please provide a 'message' (or 'question').");
            model.addAttribute("ran", false);
            model.addAttribute("prompt", "");
            return "staffing-form";
        }

        StaffOpsResponse response = tool.staffOps(text);

        model.addAttribute("ran", true);
        model.addAttribute("prompt", text);
        model.addAttribute("action", response.action());
        model.addAttribute("note", response.note());
        model.addAttribute("snapshot", response.snapshot());
        model.addAttribute("plan", response.plan());
        model.addAttribute("apply", response.apply());

        return "sqlstaff"; // re-render same page with results
    }
}
