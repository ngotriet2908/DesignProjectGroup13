package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.*;
import com.group13.tcsprojectgrading.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.*;

import static com.group13.tcsprojectgrading.models.PrivilegeEnum.*;

@RestController
@RequestMapping("/api/courses/{courseId}/projects/{projectId}/submissions")
public class SubmissionController {

    private final CanvasApi canvasApi;
    private final SubmissionService submissionService;
    private final SecurityService securityService;

    @Autowired
    public SubmissionController(CanvasApi canvasApi, SubmissionService submissionService, SecurityService securityService) {
        this.canvasApi = canvasApi;
        this.submissionService = submissionService;
        this.securityService = securityService;
    }

    @GetMapping(value = "")
    protected JsonNode getSubmissions(@PathVariable String courseId, @PathVariable String projectId, Principal principal) throws JsonProcessingException {
        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null && privileges.contains(SUBMISSIONS_READ))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode resultNode = submissionService.getSubmission(courseId, projectId, principal.getName());
        resultNode.set("course", objectMapper.readTree(courseString));

        return resultNode;
    }

    @GetMapping(value = "/{id}")
    protected JsonNode getSubmissionInfo(@PathVariable String courseId,
                                         @PathVariable String projectId,
                                         @PathVariable String id,
                                         Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {
        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null
                && (privileges.contains(SUBMISSION_READ_ALL) || privileges.contains(SUBMISSION_READ_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        String courseString = this.canvasApi.getCanvasCoursesApi().getUserCourse(courseId);

        ObjectNode resultNode = submissionService.getSubmissionInfo(courseId, projectId, id, principal.getName(), privileges);
        ObjectMapper objectMapper = new ObjectMapper();
        resultNode.set("course", objectMapper.readTree(courseString));

        return resultNode;
    }

    @PostMapping(value = "/{submissionId}/assessmentManagement")
    protected ArrayNode createNewAssessment(@PathVariable String courseId,
                                         @PathVariable String projectId,
                                         @PathVariable String submissionId,
                                         @RequestBody JsonNode object,
                                         Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {
        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null
                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return submissionService.assessmentManagement(courseId, projectId, submissionId, object, privileges, principal.getName());
    }

    @PostMapping(value = "/{submissionId}/addParticipant/{participantId}/{assessmentId}")
    protected ObjectNode addParticipantToSubmission(@PathVariable String courseId,
                                            @PathVariable String projectId,
                                            @PathVariable String submissionId,
                                            @PathVariable String participantId,
                                            @PathVariable String assessmentId,
                                            Principal principal) throws JsonProcessingException {
        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null
                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return submissionService.addParticipantToSubmission(courseId, projectId, submissionId, participantId, assessmentId, privileges, principal.getName());
    }

//    @DeleteMapping(value = "/{submissionId}/removeParticipant/{participantId}")
//    protected ObjectNode removeParticipantFromSubmission(@PathVariable String courseId,
//                                                    @PathVariable String projectId,
//                                                    @PathVariable String submissionId,
//                                                    @PathVariable String participantId,
//                                                    Principal principal) throws JsonProcessingException {
//        List<PrivilegeEnum> privileges = securityService
//                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
//        if (!(privileges != null
//                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
//        }
//
//        return submissionService.removeParticipantFromSubmission(courseId, projectId, submissionId, participantId, privileges, principal.getName(), false);
//    }

    @DeleteMapping(value = "/{submissionId}/removeParticipant/{participantId}")
    protected ObjectNode removeParticipantFromSubmission(@PathVariable String courseId,
                                                         @PathVariable String projectId,
                                                         @PathVariable String submissionId,
                                                         @PathVariable String participantId,
                                                         @RequestParam("returnAllSubmissions") boolean returnAll,
                                                         Principal principal) throws JsonProcessingException {
        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null
                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
        System.out.println("return all: " + returnAll);
        return submissionService.removeParticipantFromSubmission(courseId, projectId, submissionId, participantId, privileges, principal.getName(), returnAll);
    }

    @PostMapping(value = "/{id}/flag")
    protected JsonNode addFlag(@PathVariable String courseId,
                                         @PathVariable String projectId,
                                         @PathVariable String id,
                                         @RequestBody ObjectNode flag,
                                         Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {
        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null
                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return submissionService.addFlag(courseId, projectId, id, flag, principal.getName(), privileges);
    }

    @PostMapping(value = "/{id}/flag/create")
    protected JsonNode createFlag(@PathVariable String courseId,
                               @PathVariable String projectId,
                               @PathVariable String id,
                               @RequestBody ObjectNode flag,
                               Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {

        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null
                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return submissionService.createFlag(courseId, projectId, id, flag, principal.getName(), privileges);
    }

    @DeleteMapping(value = "/{id}/flag/{flagId}")
    protected JsonNode deleteFlag(@PathVariable String courseId,
                               @PathVariable String projectId,
                               @PathVariable String id,
                               @PathVariable String flagId,
                               Principal principal
//                                   @RequestParam Map<String, String> queryParameters
    ) throws JsonProcessingException {
        List<PrivilegeEnum> privileges = securityService
                .getPrivilegesFromUserIdAndProject(principal.getName(), courseId, projectId);
        if (!(privileges != null
                && (privileges.contains(SUBMISSION_EDIT_ALL) || privileges.contains(SUBMISSION_EDIT_SINGLE)))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        return submissionService.deleteFlag(courseId, projectId, id, flagId, principal.getName(), privileges);
    }

//    private double submissionProgress(SubmissionAssessment assessment, Rubric rubric) {
//        List<Element> criteria = rubric.fetchAllCriteria();
//        return assessment.getGradedCriteria(criteria).size()*1.0/criteria.size()*100;
//    }

}