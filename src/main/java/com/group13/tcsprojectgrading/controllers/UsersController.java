package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.Activity;
import com.group13.tcsprojectgrading.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
class UsersController {
    private final CanvasApi canvasApi;

    private final ActivityService activityService;

    @Autowired
    public UsersController(CanvasApi canvasApi, ActivityService activityService) {
        this.canvasApi = canvasApi;
        this.activityService = activityService;
    }

    @RequestMapping(value = "/self", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    protected ResponseEntity<String> selfInfo() {
        String response = this.canvasApi.getCanvasUsersApi().getAccount();

        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/recent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    protected List<JsonNode> recentProject(Principal principal) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<JsonNode> nodes = new ArrayList<>();
        for(Activity activity: activityService.getActivities(principal.getName())) {
            ObjectNode node = objectMapper.createObjectNode();

            node.put("id", activity.getProjectId());
            node.put("course_id", activity.getCourseId());
            node.put("name", activity.getProjectName());
            node.put("created_at", activity.getProjectCreatedAt().toString());
            nodes.add(node);
        }

        return nodes.subList(0, Math.min(3, nodes.size()));
    }
}