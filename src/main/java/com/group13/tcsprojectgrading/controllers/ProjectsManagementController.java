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

//    @GetMapping(value = "")
//    @ResponseBody
//    protected JsonNode getManagementInfo(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null && privileges.contains(MANAGE_GRADERS_OPEN))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//
//        return projectsManagementService.getManagementInfo(courseId, projectId, principal.getName());
//    }
//
//    @PostMapping(value = "/addGraders")
//    protected JsonNode addGrader(@PathVariable String courseId,
//                                 @PathVariable String projectId,
//                                 @RequestBody ArrayNode activeGraders,
//                                 Principal principal) throws JsonProcessingException, ParseException {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null && privileges.contains(MANAGE_GRADERS_EDIT))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<String> gradersResponse = this.canvasApi.getCanvasCoursesApi().getCourseGraders(courseId);
//        ArrayNode gradersArrayFromCanvas = groupPages(objectMapper, gradersResponse);
//        return projectsManagementService.addGrader(courseId, projectId, principal.getName(), gradersArrayFromCanvas, activeGraders);
//    }
//
//
//
//    @GetMapping(value = "/addGraders/getAllGraders")
//    @ResponseBody
//    protected ArrayNode getActiveGrader(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException, ParseException {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null && privileges.contains(MANAGE_GRADERS_OPEN))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//
//        List<String> gradersResponse = this.canvasApi.getCanvasCoursesApi().getCourseGraders(courseId);
//        ObjectMapper objectMapper = new ObjectMapper();
//        ArrayNode results = objectMapper.createArrayNode();
//        ArrayNode gradersArrayFromCanvas = groupPages(objectMapper, gradersResponse);
//        for (Iterator<JsonNode> it = gradersArrayFromCanvas.elements(); it.hasNext(); ) {
//            JsonNode node = it.next();
//            ObjectNode grader = objectMapper.createObjectNode();
//            grader.put("id", node.get("id").asText());
//            grader.put("name", node.get("name").asText());
//            grader.put("role", RoleEnum.getRoleFromEnrolment(node.get("enrollments").get(0).get("type").asText()).toString());
//            results.add(grader);
//        }
//        return results;
//    }
//
//    @GetMapping(value = "/assign/{id}/{toUserId}")
//    @ResponseBody
//    protected JsonNode assignSubmission(@PathVariable String courseId,
//                                        @PathVariable String projectId,
//                                        @PathVariable String id,
////                                  @PathVariable String fromUserId,
//                                        @PathVariable String toUserId,
//                                        Principal principal) throws JsonProcessingException, ParseException {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null && privileges.contains(MANAGE_GRADERS_EDIT))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//
//        return projectsManagementService.assignSubmission(courseId, projectId, id, toUserId);
//    }
//
//    @GetMapping(value = "/return/{userId}")
//    @ResponseBody
//    protected JsonNode returnTasks(@PathVariable String courseId,
//                                   @PathVariable String projectId,
//                                   @PathVariable String userId,
//                                   Principal principal) throws JsonProcessingException, ParseException {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null && privileges.contains(MANAGE_GRADERS_SELF_EDIT))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//
//        return projectsManagementService.returnSubmissions(courseId, projectId, userId);
//    }
//
//    @PostMapping(value = "/bulkAssign")
//    protected JsonNode bulkAssign(@PathVariable String courseId,
//                                  @PathVariable String projectId,
//                                  @RequestBody ObjectNode object,
//                                  Principal principal) throws JsonProcessingException, ParseException {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null && privileges.contains(MANAGE_GRADERS_EDIT))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//
//        return projectsManagementService.bulkAssign(courseId, projectId, object);
//    }

}