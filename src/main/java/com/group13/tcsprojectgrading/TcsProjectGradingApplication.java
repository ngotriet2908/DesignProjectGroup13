package com.group13.tcsprojectgrading;

import com.group13.tcsprojectgrading.models.permissions.Privilege;
import com.group13.tcsprojectgrading.models.permissions.Role;
import com.group13.tcsprojectgrading.models.permissions.PrivilegeEnum;
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


//    @Override
//    public void run(String... args) {
//        repository.deleteAll();
////        repository.save(new Rubric("v1", new ArrayList<>()));
//
//        // fetch all customers
////        System.out.println("Rubrics found with findAll():");
////        System.out.println("-------------------------------");
////        for (Rubric rubric : repository.findAll()) {
////            System.out.println(rubric);
////        }
////        System.out.println();
//    }

    @Bean
    public CommandLineRunner demoData(RoleService roleService, PrivilegeService privilegeService) {
        return args -> {

            List<PrivilegeEnum> teacherPrivilegesEnum = List.of(
                    MANAGE_GRADERS_OPEN, MANAGE_GRADERS_EDIT, MANAGE_GRADERS_SELF_EDIT,
                    RUBRIC_READ, RUBRIC_WRITE, RUBRIC_DOWNLOAD,
                    STATISTIC_READ, STATISTIC_WRITE,
                    ADMIN_TOOLBAR_VIEW,
                    TODO_LIST_VIEW,
                    GRADING_WRITE_ALL, GRADING_READ_ALL,
                    PROJECT_READ,
                    SUBMISSIONS_SYNC, SUBMISSIONS_READ,
                    SUBMISSION_READ_ALL, SUBMISSION_EDIT_ALL,
                    FEEDBACK_SEND, FEEDBACK_OPEN,
                    FLAG_CREATE, FLAG_DELETE, FLAG_ASSIGN
            );

            List<PrivilegeEnum> taPrivilegesEnum = List.of(
                    MANAGE_GRADERS_OPEN, MANAGE_GRADERS_SELF_EDIT,
                    RUBRIC_READ, RUBRIC_DOWNLOAD,
                    STATISTIC_READ,
                    ADMIN_TOOLBAR_VIEW,
                    TODO_LIST_VIEW,
                    GRADING_WRITE_SINGLE, GRADING_READ_ALL,
                    PROJECT_READ,
                    SUBMISSIONS_READ,
                    SUBMISSION_READ_ALL, SUBMISSION_EDIT_SINGLE,
                    FLAG_CREATE, FLAG_ASSIGN
            );

            List<Privilege> teacherPrivileges = new ArrayList<>();
            teacherPrivilegesEnum.forEach(privilegeEnum -> teacherPrivileges.add(privilegeService.addPrivilegeIfNotExist(privilegeEnum.toString())));

            List<Privilege> taPrivileges = new ArrayList<>();
            taPrivilegesEnum.forEach(privilegeEnum -> taPrivileges.add(privilegeService.addPrivilegeIfNotExist(privilegeEnum.toString())));

            Role teacherRole = roleService.addRoleIfNotExist(
                    RoleEnum.TEACHER.toString(),
                    teacherPrivileges
            );

            Role taRole = roleService.addRoleIfNotExist(
                    RoleEnum.TA.toString(),
                    taPrivileges
            );

//            Role studentRole = roleService.addRoleIfNotExist(
//                    RoleEnum.STUDENT.toString(),
//
//            );
        };
    }
}
