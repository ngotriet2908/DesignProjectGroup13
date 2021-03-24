package com.group13.tcsprojectgrading;

import com.group13.tcsprojectgrading.models.Privilege;
import com.group13.tcsprojectgrading.models.Role;
import com.group13.tcsprojectgrading.models.PrivilegeEnum;
import com.group13.tcsprojectgrading.services.PrivilegeService;
import com.group13.tcsprojectgrading.models.RoleEnum;
import com.group13.tcsprojectgrading.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

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
            Privilege mangeGradersOpen = privilegeService.addPrivilegeIfNotExist(PrivilegeEnum.MANAGE_GRADERS_OPEN.toString());

            Privilege rubricRead = privilegeService.addPrivilegeIfNotExist(PrivilegeEnum.RUBRIC_READ.toString());
            Privilege rubricWrite = privilegeService.addPrivilegeIfNotExist(PrivilegeEnum.RUBRIC_WRITE.toString());

            Privilege statsRead = privilegeService.addPrivilegeIfNotExist(PrivilegeEnum.STATISTIC_READ.toString());
            Privilege statsWrite = privilegeService.addPrivilegeIfNotExist(PrivilegeEnum.STATISTIC_WRITE.toString());

            Privilege studentView = privilegeService.addPrivilegeIfNotExist(PrivilegeEnum.STUDENT_PERSONAL_VIEW.toString());

            Privilege adminToolBarView = privilegeService.addPrivilegeIfNotExist(PrivilegeEnum.ADMIN_TOOLBAR_VIEW.toString());
            Privilege todoListView = privilegeService.addPrivilegeIfNotExist(PrivilegeEnum.TODO_LIST_VIEW.toString());

            Privilege gradingWriteSingle = privilegeService.addPrivilegeIfNotExist(PrivilegeEnum.GRADING_WRITE_SINGLE.toString());
            Privilege gradingWriteAll = privilegeService.addPrivilegeIfNotExist(PrivilegeEnum.GRADING_WRITE_ALL.toString());
            Privilege gradingRead = privilegeService.addPrivilegeIfNotExist(PrivilegeEnum.GRADING_READ.toString());


            Role teacherRole = roleService.addRoleIfNotExist(
                    RoleEnum.TEACHER.toString(),
                    List.of(
//                            studentView
                            mangeGradersOpen,
                            rubricRead,
                            rubricWrite,
                            statsRead,
                            adminToolBarView,
                            todoListView,
                            statsWrite,
                            gradingRead,
                            gradingWriteSingle
                            )
            );

            Role taRole = roleService.addRoleIfNotExist(
                    RoleEnum.TA.toString(),
                    List.of(mangeGradersOpen,
                            rubricRead,
                            statsRead,
                            adminToolBarView,
                            todoListView
                            )
            );

            Role studentRole = roleService.addRoleIfNotExist(
                    RoleEnum.STUDENT.toString(),
                    List.of(studentView
                    )
            );
        };
    }
}
