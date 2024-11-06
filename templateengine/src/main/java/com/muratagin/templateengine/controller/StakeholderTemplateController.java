package com.muratagin.templateengine.controller;

import com.muratagin.templateengine.service.StakeholderTemplateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class StakeholderTemplateController {

    private final StakeholderTemplateService stakeholderTemplateService;

    public StakeholderTemplateController(StakeholderTemplateService stakeholderTemplateService) {
        this.stakeholderTemplateService = stakeholderTemplateService;
    }

    @RequestMapping(value = "/process", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> processRequest(HttpServletRequest request, HttpServletResponse response) {

        String stakeholderIdHeader = request.getHeader("Stakeholder-Id");
        if (stakeholderIdHeader == null) {
            return ResponseEntity.badRequest().body("Missing Stakeholder-Id header");
        }

        try {
            UUID stakeholderId = UUID.fromString(stakeholderIdHeader);
            stakeholderTemplateService.processRequest(request, stakeholderId);
            return ResponseEntity.ok("Request processed");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request - " + e.getMessage());
        }
    }
}
