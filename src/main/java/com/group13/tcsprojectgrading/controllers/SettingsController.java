package com.group13.tcsprojectgrading.controllers;

import com.group13.tcsprojectgrading.models.settings.Settings;
import com.group13.tcsprojectgrading.services.settings.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;

/**
 * Controller handles Email Settings Endpoints
 */
@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/settings")
public class SettingsController {
    private final SettingsService settingsService;

    @Autowired
    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * get user's settings.
     * @param projectId canvas project id
     * @param principal injected oauth2 client's information
     * @return a Settings from database
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    protected Settings getSettings(@PathVariable Long projectId,
                                   Principal principal) {
        return settingsService.getSettings(projectId, Long.valueOf(principal.getName()));
    }

    /**
     * Performs a full update of settings.
     * @param settings user updated Settings from front-end
     * @return status message
     */
    @RequestMapping(value = "", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<?> saveSettings(@RequestBody Settings settings) {
        this.settingsService.saveSettings(settings);
        return ResponseEntity.ok("Resource saved");
    }
}