package com.group13.tcsprojectgrading.services.graders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group13.tcsprojectgrading.models.graders.Grader;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.permissions.RoleEnum;
import com.group13.tcsprojectgrading.models.submissions.Submission;
import com.group13.tcsprojectgrading.services.ProjectService;
import com.group13.tcsprojectgrading.services.permissions.ProjectRoleService;
import com.group13.tcsprojectgrading.services.permissions.RoleService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class ProjectsManagementService {
    private final GraderService graderService;
    private final ProjectService projectService;
    private final RoleService roleService;
    private final ProjectRoleService projectRoleService;
    private final SubmissionService submissionService;
    private final RubricService rubricService;

    public ProjectsManagementService(GraderService graderService, ProjectService projectService, RoleService roleService, ProjectRoleService projectRoleService, SubmissionService submissionService, RubricService rubricService) {
        this.graderService = graderService;
        this.projectService = projectService;
        this.roleService = roleService;
        this.projectRoleService = projectRoleService;
        this.submissionService = submissionService;
        this.rubricService = rubricService;
    }

    @Transactional
    public JsonNode getManagementInfo(String courseId, String projectId, String userId) {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        ArrayNode notAssignedArray = objectMapper.createArrayNode();
        ArrayNode gradersArray = objectMapper.createArrayNode();

        //sync submissions <-> canvas submissions

        //sync submissions <-> canvas submissions

        List<Grader> graders = graderService.getGraderFromProject(project);
        Map<String, ArrayNode> graderMap = new HashMap<>();
        for (Grader grader: graders) {
            ObjectNode formatNode = objectMapper.createObjectNode();
            ArrayNode tasksArray = objectMapper.createArrayNode();
            graderMap.put(grader.getUserId(), tasksArray);
            formatNode.put("id", grader.getUserId());
            formatNode.put("name", grader.getName());
            formatNode.set("role", grader.getRolesArrayNode());
            formatNode.set("privileges", grader.getPrivilegesArrayNode());
            formatNode.set("groups", tasksArray);
            gradersArray.add(formatNode);
        }

//        List<Task> tasks = taskService.getTasksFromId(project);

        List<Submission> submissions = submissionService.findSubmissionWithProject(project);
        for(Submission submission: submissions) {

            ObjectNode taskNode = objectMapper.createObjectNode();
            taskNode.put("id", submission.getId().toString());
//            ((ObjectNode) taskNode).put("submission_id", submission.getId());
            taskNode.put("isGroup", submission.getGroupId() != null);
            taskNode.put("name", submission.getName());

            if (submission.getGrader() == null) {
                notAssignedArray.add(taskNode);
            } else {
                graderMap.get(submission.getGrader().getUserId()).add(taskNode);
            }
        }

        resultNode.set("graders", gradersArray);
        resultNode.set("notAssigned", notAssignedArray);

        return resultNode;
    }

    @Transactional(rollbackOn = Exception.class)
    public JsonNode addGrader(String courseId, String projectId, String userId, ArrayNode gradersArrayFromCanvas, ArrayNode activeGraders) {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }

        List<Grader> gradersDatabase = graderService.getGraderFromProject(project);
        List<String> editedActiveGraders = new ArrayList<>();
        Map<String, Grader> availableGrader = new HashMap<>();
        for (Iterator<JsonNode> it = gradersArrayFromCanvas.elements(); it.hasNext(); ) {
            JsonNode node = it.next();
            availableGrader.put(node.get("id").asText(), new Grader(
                    project,
                    node.get("id").asText(),
                    node.get("name").asText(),
                    //TODO check whether enrolment contains more than 1 course
                    projectRoleService.findByProjectAndRole(project, roleService.findRoleByName(RoleEnum.getRoleFromEnrolment(node.get("enrollments").get(0).get("type").asText()).toString()))
            ));
        }

        for (Iterator<JsonNode> it = activeGraders.elements(); it.hasNext(); ) {
            JsonNode grader = it.next();
            editedActiveGraders.add(grader.get("id").asText());
        }
        for (Grader grader: gradersDatabase) {
            if (!editedActiveGraders.contains(grader.getUserId())) {
                graderService.deleteGrader(grader);
            }
        }

        for (String activeGraderId: editedActiveGraders) {
            graderService.addNewGrader(availableGrader.get(activeGraderId));
        }

        //remake notAssigned & graders
        return remakeNotAssignedAndGraders(project);
    }

    @Transactional(rollbackOn = Exception.class)
    public JsonNode assignSubmission(String courseId, String projectId, String id, String toUserId) {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        Submission submission = submissionService.findSubmissionById(id);
        Grader grader = graderService.getGraderFromGraderId(toUserId, project);
        if (submission == null) return null;
        if (!toUserId.equals("notAssigned")) {
            if (grader == null) return null;
            submission.setGrader(grader);
        } else {
//            System.out.println("notAssigned");
            submission.setGrader(null);
        }
        submissionService.saveGrader(submission);

        //remake notAssigned & graders
        return remakeNotAssignedAndGraders(project);
    }

    @Transactional(rollbackOn = Exception.class)
    public JsonNode returnSubmissions(String courseId, String projectId, String userId) {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        List<Submission> submissions = submissionService.findSubmissionWithProject(project);
        for(Submission submission: submissions) {
            if (submission.getGrader() == null) continue;
            if (submission.getGrader().getUserId().equals(userId)) {
                submission.setGrader(null);
                submissionService.saveGrader(submission);
            }
        }

        return remakeNotAssignedAndGraders(project);
    }

    @Transactional(rollbackOn = Exception.class)
    public JsonNode bulkAssign(String courseId, String projectId, ObjectNode object) {
        Project project = projectService.getProjectById(courseId, projectId);
        if (project == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        List<Submission> submissions = submissionService.findSubmissionWithProject(project);

        List<Submission> notAssigned = new ArrayList<>();
        for(Submission submission: submissions) {
            if (submission.getGrader() == null) {
                notAssigned.add(submission);
            }
        }

        int notAssignNum = object.get("submissions").asInt();
        if (notAssignNum > notAssigned.size()) {
//            System.out.println("different sync");
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Collections.shuffle(notAssigned);
        ArrayNode gradersNeedToAssign = (ArrayNode) object.get("graders");
        List<JsonNode> gradersNeedToBeAssigned = new ArrayList<>();
        for (Iterator<JsonNode> it = gradersNeedToAssign.elements(); it.hasNext(); ) {
            JsonNode node = it.next();
            gradersNeedToBeAssigned.add(node);
        }

        int[] amountOfTasks = new int[gradersNeedToBeAssigned.size()];

        Arrays.fill(amountOfTasks, (int) notAssignNum / amountOfTasks.length);
        Random rand = new Random();
        notAssignNum -= amountOfTasks.length*((int) notAssignNum / amountOfTasks.length);

        while(notAssignNum > 0) {
            int random = rand.nextInt(amountOfTasks.length);
            amountOfTasks[random] += 1;
            notAssignNum -= 1;
        }

        for(int i = 0; i < gradersNeedToBeAssigned.size(); i++) {
            JsonNode grader = gradersNeedToBeAssigned.get(i);
            int num = amountOfTasks[i];
            for(int j = 0; j < num; j++) {
                if (notAssigned.size() == 0) {
//                    System.out.println("something is wrong, not assigned is overflow");
                    break;
                }
                Submission submission = notAssigned.remove(0);
                Grader grader1 = graderService.getGraderFromGraderId(grader.get("id").asText(), project);
                if (grader1 == null) {
//                    System.out.println("Grader not found");
                    return null;
                }
                submission.setGrader(grader1);
                submissionService.saveGrader(submission);
            }
        }

        return remakeNotAssignedAndGraders(project);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public JsonNode remakeNotAssignedAndGraders(Project project) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Grader> graders = graderService.getGraderFromProject(project);
        ObjectNode resultNode = objectMapper.createObjectNode();
        ArrayNode notAssignedArray = objectMapper.createArrayNode();
        ArrayNode gradersArray = objectMapper.createArrayNode();

        Map<String, ArrayNode> graderMap = new HashMap<>();
        for (Grader grader1: graders) {
            ObjectNode formatNode = objectMapper.createObjectNode();
            ArrayNode tasksArray = objectMapper.createArrayNode();
            graderMap.put(grader1.getUserId(), tasksArray);
            formatNode.put("id", grader1.getUserId());
            formatNode.put("name", grader1.getName());
            formatNode.set("role", grader1.getRolesArrayNode());
            formatNode.set("privileges", grader1.getPrivilegesArrayNode());
            formatNode.set("groups", tasksArray);
            gradersArray.add(formatNode);
        }

        List<Submission> submissions = submissionService.findSubmissionWithProject(project);
        for(Submission submission: submissions) {

            ObjectNode taskNode = objectMapper.createObjectNode();
            taskNode.put("id", submission.getId().toString());
//            ((ObjectNode) taskNode).put("submission_id", submission.getId());
            taskNode.put("isGroup", submission.getGroupId() != null);
            taskNode.put("name", submission.getName());

            if (submission.getGrader() == null) {
                notAssignedArray.add(taskNode);
            } else {
                graderMap.get(submission.getGrader().getUserId()).add(taskNode);
            }
        }

        resultNode.set("graders", gradersArray);
        resultNode.set("notAssigned", notAssignedArray);

        return resultNode;
    }
}
