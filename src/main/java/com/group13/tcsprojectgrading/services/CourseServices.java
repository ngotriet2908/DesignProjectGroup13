package com.group13.tcsprojectgrading.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.canvas.api.CanvasApi;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.RoleEnum;
import com.group13.tcsprojectgrading.models.Submission;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;

import static com.group13.tcsprojectgrading.controllers.Utils.groupPages;

@Service
public class CourseServices {
    private final ProjectService projectService;
    private final SubmissionService submissionService;

    @Autowired
    public CourseServices(ProjectService projectService, SubmissionService submissionService) {
        this.projectService = projectService;
        this.submissionService = submissionService;
    }

    @Transactional
    public JsonNode getCourse(String course_id, CanvasApi canvasApi, String userId) throws JsonProcessingException {
        String courseString = canvasApi.getCanvasCoursesApi().getUserCourse(course_id);
        List<Project> projects = projectService.getProjectsByCourseId(course_id);

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        JsonNode jsonCourseNode = objectMapper.readTree(courseString);

        for(Project project: projects) {
//            ObjectNode projectNode = objectMapper.createObjectNode();
            String nodeString = canvasApi.getCanvasCoursesApi().getCourseProject(project.getCourseId(), project.getProjectId());
            JsonNode node = objectMapper.readTree(nodeString);
//            projectNode.put("id", node.get("id").asText());
//            projectNode.put("name", node.get("name").asText());
            arrayNode.add(node);
        }

        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("course", jsonCourseNode);
        resultNode.set("projects", arrayNode);
//        JsonNode resultNode = objectMapper.createObjectNode();
        String userResponse = canvasApi.getCanvasCoursesApi().getCourseUser(course_id, userId);
        ArrayNode enrolmentsNode = groupPages(objectMapper, canvasApi.getCanvasUsersApi().getEnrolments(userId));
        JsonNode userJson = objectMapper.readTree(userResponse);

        RoleEnum roleEnum = null;

        for (Iterator<JsonNode> it = enrolmentsNode.elements(); it.hasNext(); ) {
            JsonNode enrolmentNode = it.next();
            if (enrolmentNode.get("course_id").asText().equals(course_id)) {
                roleEnum = RoleEnum.getRoleFromEnrolment(enrolmentNode.get("role").asText());
            }
        }

        if (roleEnum == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Enrolment not found"
            );
        }

        if (roleEnum.equals(RoleEnum.TEACHER)) {
            ((ObjectNode) userJson).put("role", "teacher");
        } else if (roleEnum.equals(RoleEnum.TA)) {
            ((ObjectNode) userJson).put("role", "ta");
        }

        ((ObjectNode)resultNode).set("course", jsonCourseNode);
        ((ObjectNode)resultNode).set("projects", arrayNode);
        ((ObjectNode)resultNode).set("user", userJson);

        if (roleEnum.equals(RoleEnum.TEACHER) || roleEnum.equals(RoleEnum.TA)) {
            List<Submission> submissions = submissionService.findSubmissionsForGraderCourse(userId, course_id);
            Map<Project, List<Submission>> projectListMap = new HashMap<>();

            for(Submission submission : submissions) {
                if (!projectListMap.containsKey(submission.getProject())) {
                    List<Submission> tasks1 = new ArrayList<>();
                    tasks1.add(submission);
                    projectListMap.put(submission.getProject(), tasks1);
                } else {
                    projectListMap.get(submission.getProject()).add(submission);
                }
            }

            ArrayNode arrayNode1 = objectMapper.createArrayNode();

            for(Map.Entry<Project, List<Submission>> entry: projectListMap.entrySet()) {
                JsonNode node = objectMapper.createObjectNode();

                ((ObjectNode) node).set("course", jsonCourseNode);
                ((ObjectNode) node).set("project", entry.getKey().convertToJson());
                ((ObjectNode) node).put("submissions", entry.getValue().size());
                ((ObjectNode) node).put("progress", (int)(Math.random()*100));

                arrayNode1.add(node);
            }
            ((ObjectNode)resultNode).set("tasks", arrayNode1);
        }

        if (arrayNode == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "403");
        } else {
            return resultNode;
        }


    }
}
