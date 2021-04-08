package com.group13.tcsprojectgrading.services.graders;

import com.group13.tcsprojectgrading.services.project.ProjectService;
import com.group13.tcsprojectgrading.services.permissions.ProjectRoleService;
import com.group13.tcsprojectgrading.services.permissions.RoleService;
import com.group13.tcsprojectgrading.services.rubric.RubricService;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.stereotype.Service;

@Service
public class ProjectsManagementService {
    private final GradingParticipationService gradingParticipationService;
    private final ProjectService projectService;
    private final RoleService roleService;
    private final ProjectRoleService projectRoleService;
    private final SubmissionService submissionService;
    private final RubricService rubricService;

    public ProjectsManagementService(GradingParticipationService gradingParticipationService, ProjectService projectService, RoleService roleService, ProjectRoleService projectRoleService, SubmissionService submissionService, RubricService rubricService) {
        this.gradingParticipationService = gradingParticipationService;
        this.projectService = projectService;
        this.roleService = roleService;
        this.projectRoleService = projectRoleService;
        this.submissionService = submissionService;
        this.rubricService = rubricService;
    }

//    @Transactional(rollbackOn = Exception.class)
//    public JsonNode bulkAssign(String courseId, String projectId, ObjectNode object) {
//        Project project = projectService.getProjectById(courseId, projectId);
//        if (project == null) {
//            throw new ResponseStatusException(
//                    HttpStatus.NOT_FOUND, "entity not found"
//            );
//        }
//        List<Submission> submissions = submissionService.findSubmissionWithProject(project);
//
//        List<Submission> notAssigned = new ArrayList<>();
//        for(Submission submission: submissions) {
//            if (submission.getGrader() == null) {
//                notAssigned.add(submission);
//            }
//        }
//
//        int notAssignNum = object.get("submissions").asInt();
//        if (notAssignNum > notAssigned.size()) {
////            System.out.println("different sync");
//            return null;
//        }
//        ObjectMapper objectMapper = new ObjectMapper();
//        Collections.shuffle(notAssigned);
//        ArrayNode gradersNeedToAssign = (ArrayNode) object.get("graders");
//        List<JsonNode> gradersNeedToBeAssigned = new ArrayList<>();
//        for (Iterator<JsonNode> it = gradersNeedToAssign.elements(); it.hasNext(); ) {
//            JsonNode node = it.next();
//            gradersNeedToBeAssigned.add(node);
//        }
//
//        int[] amountOfTasks = new int[gradersNeedToBeAssigned.size()];
//
//        Arrays.fill(amountOfTasks, (int) notAssignNum / amountOfTasks.length);
//        Random rand = new Random();
//        notAssignNum -= amountOfTasks.length*((int) notAssignNum / amountOfTasks.length);
//
//        while(notAssignNum > 0) {
//            int random = rand.nextInt(amountOfTasks.length);
//            amountOfTasks[random] += 1;
//            notAssignNum -= 1;
//        }
//
//        for(int i = 0; i < gradersNeedToBeAssigned.size(); i++) {
//            JsonNode grader = gradersNeedToBeAssigned.get(i);
//            int num = amountOfTasks[i];
//            for(int j = 0; j < num; j++) {
//                if (notAssigned.size() == 0) {
////                    System.out.println("something is wrong, not assigned is overflow");
//                    break;
//                }
//                Submission submission = notAssigned.remove(0);
//                GradingParticipation grader1 = graderService.getGraderFromGraderId(grader.get("id").asText(), project);
//                if (grader1 == null) {
////                    System.out.println("Grader not found");
//                    return null;
//                }
//                submission.setGrader(grader1);
//                submissionService.saveGrader(submission);
//            }
//        }
//
//        return remakeNotAssignedAndGraders(project);
//    }
}
