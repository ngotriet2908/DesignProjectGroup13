package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.group13.tcsprojectgrading.models.settings.Settings;
import com.group13.tcsprojectgrading.services.settings.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/settings")
public class SettingsController {
    private final SettingsService settingsService;

    @Autowired
    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    protected Settings getSettings(@PathVariable String courseId,
                                   @PathVariable String projectId,
                                   Principal principal) {
        return settingsService.getOrCreateSettings(courseId, projectId, principal.getName());
    }

    /*
    Performs a full update of settings
    TODO: switch to partial updates
     */
    @RequestMapping(value = "", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<?> saveSettings(@RequestBody Settings settings) {
        this.settingsService.saveSettings(settings);
        return ResponseEntity.ok("Resource saved");
    }

//    @RequestMapping(value = "", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> updateSettingsPartially(
//            @RequestBody Map<String, Object> updates,
//            @PathVariable String courseId,
//            @PathVariable String projectId,
//            Principal principal) {
//
//        for (Object o : updates.keySet()) {
//            System.out.println(o);
//        }
//
//        this.settingsService.updateSettings(courseId, projectId, principal.getName(), updates);
//        return ResponseEntity.ok("Resource updated");
//    }
}