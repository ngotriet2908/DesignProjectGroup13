package com.group13.tcsprojectgrading.controllers;

import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.services.graders.GradingParticipationService;
import com.group13.tcsprojectgrading.services.graders.ProjectsManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/management")
public class ProjectsManagementController {
    private final CanvasApi canvasApi;
    private final ProjectsManagementService projectsManagementService;
    private final GradingParticipationService gradingParticipation;

    @Autowired
    public ProjectsManagementController(CanvasApi canvasApi, ProjectsManagementService projectsManagementService, GradingParticipationService gradingParticipation) {
        this.canvasApi = canvasApi;
        this.projectsManagementService = projectsManagementService;
        this.gradingParticipation = gradingParticipation;
    }

//    @PostMapping(value = "/bulkAssign")
//    protected JsonNode bulkAssign(@PathVariable String courseId,
//                                  @PathVariable String projectId,
//                                  @RequestBody ObjectNode object,
//                                  Principal principal) throws JsonProcessingException, ParseException {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null && privileges.contains(MANAGE_GRADERS_EDIT))) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "unauthorized");
//        }
//
//        return projectsManagementService.bulkAssign(courseId, projectId, object);
//    }
}