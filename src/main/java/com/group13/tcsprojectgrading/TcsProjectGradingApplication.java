package com.group13.tcsprojectgrading;

import com.group13.tcsprojectgrading.models.grading.IssueStatusEnum;
import com.group13.tcsprojectgrading.models.permissions.Privilege;
import com.group13.tcsprojectgrading.models.permissions.Role;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
//import com.group13.tcsprojectgrading.services.grading.IssueService;
import com.group13.tcsprojectgrading.services.permissions.PrivilegeService;
import com.group13.tcsprojectgrading.models.permissions.RoleEnum;
import com.group13.tcsprojectgrading.services.permissions.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

import static com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum.*;

@SpringBootApplication
//public class TcsProjectGradingApplication implements CommandLineRunner {
public class TcsProjectGradingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcsProjectGradingApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoData(RoleService roleService, AssessmentService assessmentService, PrivilegeService privilegeService) {
        return args -> {
            // assign privileges to roles
            List<PrivilegeEnum> teacherPrivilegesEnum = List.of(
                    MANAGE_GRADERS_OPEN, MANAGE_GRADERS_EDIT,
                    RUBRIC_READ, RUBRIC_WRITE, RUBRIC_DOWNLOAD,
                    STATISTIC_READ, STATISTIC_WRITE,
                    ADMIN_TOOLBAR_VIEW,
                    TODO_LIST_VIEW,
                    GRADING_WRITE_ALL, GRADING_READ_ALL,
                    PROJECT_READ,
                    SUBMISSIONS_SYNC, SUBMISSIONS_READ,
                    SUBMISSION_READ_ALL, SUBMISSION_EDIT_ALL,
                    FEEDBACK_SEND, FEEDBACK_OPEN, FEEDBACK_EDIT,
                    FLAG_EDIT
            );

            List<PrivilegeEnum> taPrivilegesEnum = List.of(
//                    MANAGE_GRADERS_OPEN, MANAGE_GRADERS_SELF_EDIT,
//                    RUBRIC_READ, RUBRIC_DOWNLOAD,
//                    STATISTIC_READ,
//                    ADMIN_TOOLBAR_VIEW,
//                    TODO_LIST_VIEW,
//                    GRADING_WRITE_SINGLE, GRADING_READ_ALL,
//                    PROJECT_READ,
//                    SUBMISSIONS_READ,
//                    SUBMISSION_READ_ALL, SUBMISSION_EDIT_SINGLE,
//                    FLAG_CREATE, FLAG_ASSIGN
            );

            List<PrivilegeEnum> taGradingPrivilegesEnum = List.of(
                    MANAGE_GRADERS_OPEN, MANAGE_GRADERS_SELF_EDIT,
                    RUBRIC_READ, RUBRIC_DOWNLOAD,
                    STATISTIC_READ,
                    ADMIN_TOOLBAR_VIEW,
                    TODO_LIST_VIEW,
                    GRADING_WRITE_SINGLE, GRADING_READ_ALL,
                    PROJECT_READ,
                    SUBMISSIONS_READ,
                    SUBMISSION_READ_ALL, SUBMISSION_EDIT_SINGLE,
                    FLAG_EDIT
            );

            List<Privilege> teacherPrivileges = new ArrayList<>();
            teacherPrivilegesEnum.forEach(privilegeEnum -> teacherPrivileges.add(privilegeService.addPrivilegeIfNotExist(privilegeEnum.toString())));

            List<Privilege> taPrivileges = new ArrayList<>();
            taPrivilegesEnum.forEach(privilegeEnum -> taPrivileges.add(privilegeService.addPrivilegeIfNotExist(privilegeEnum.toString())));

            List<Privilege> taGradingPrivileges = new ArrayList<>();
            taGradingPrivilegesEnum.forEach(privilegeEnum -> taGradingPrivileges.add(privilegeService.addPrivilegeIfNotExist(privilegeEnum.toString())));

            // store default roles to db
            roleService.addRoleIfNotExist(
                    RoleEnum.TEACHER.toString(),
                    teacherPrivileges
            );

            roleService.addRoleIfNotExist(
                    RoleEnum.TA.toString(),
                    taPrivileges
            );

            roleService.addRoleIfNotExist(
                    RoleEnum.STUDENT.toString(),
                    new ArrayList<>()
            );

            roleService.addRoleIfNotExist(
                    RoleEnum.TA_GRADING.toString(),
                    taGradingPrivileges
            );

            // store default issue statuses to db
            assessmentService.saveIssueStatus(
                IssueStatusEnum.OPEN
            );

            assessmentService.saveIssueStatus(
                    IssueStatusEnum.RESOLVED
            );
        };
    }
}
