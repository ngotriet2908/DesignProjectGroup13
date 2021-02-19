package com.group13.tcsprojectgrading;

import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.project.CourseGroup;
import com.group13.tcsprojectgrading.model.project.Project;
import com.group13.tcsprojectgrading.model.project.rubric.*;
import com.group13.tcsprojectgrading.model.user.*;
import com.group13.tcsprojectgrading.service.*;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class TcsProjectGradingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcsProjectGradingApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(AccountService accountService,
                                        CourseService courseService,
                                        StudentService studentService,
                                        ParticipantService participantService,
                                        TeacherService teacherService,
                                        TeachingAssistantService teachingAssistantService,
                                        ProjectService projectService,
                                        CourseGroupService courseGroupService,
                                        GroupParticipantService groupParticipantService,
                                        SubmissionService submissionService,
                                        RubricService rubricService,
                                        RubricVersionService rubricVersionService,
                                        BlockService blockService,
                                        BlockVersionService blockVersionService,
                                        CriterionService criterionService,
                                        CriterionVersionService criterionVersionService) {

        return args -> {
            Account Triet = new Account("s2188414", "Triet Ngo");
            Account Yevhen = new Account("s2093421", "Yevhen Khavrona");
            Account Dylan = new Account("s2154854", "Dylan Koldenhof");
            Account Robin = new Account("s1477196", "Robin Waterval");
            Account Adrijus = new Account("s1971514", "Adrijus Prieskienis");
            Account Omer = new Account("m1971232", "Omer Omer");

            accountService.addNewAccount(Triet);
            accountService.addNewAccount(Yevhen);
            accountService.addNewAccount(Dylan);
            accountService.addNewAccount(Robin);
            accountService.addNewAccount(Adrijus);
            accountService.addNewAccount(Omer);


            Course softwareSystem = new Course("EWI-SS", "Software System");
            Course networkSystem = new Course("EWI-NS", "Network System");

            courseService.addNewCourse(softwareSystem);
            courseService.addNewCourse(networkSystem);

            studentService.addNewStudent(softwareSystem, Dylan);
            studentService.addNewStudent(softwareSystem, Robin);
            studentService.addNewStudent(softwareSystem, Adrijus);

            studentService.addNewStudent(networkSystem, Adrijus);
            studentService.addNewStudent(networkSystem, Robin);
            studentService.addNewStudent(networkSystem, Triet);
            studentService.addNewStudent(networkSystem, Yevhen);

            teacherService.addNewTeacher(softwareSystem, Omer);

            teachingAssistantService.addNewTA(softwareSystem, Triet);
            teachingAssistantService.addNewTA(softwareSystem, Yevhen);

//            List<Student> students = studentService.getStudents();

            List<Participant> participants = participantService.findAllParticipant();
            participants.forEach(System.out::println);

            Project programmingProject = new Project("Programming project",
                    Timestamp.valueOf("2020-2-20 23:59:59.0"),
                    Timestamp.valueOf("2020-2-10 23:59:59.0"),
                    softwareSystem);

            Project designProject = new Project("design project",
                    Timestamp.valueOf("2020-1-20 23:59:59.0"),
                    Timestamp.valueOf("2020-1-10 23:59:59.0"),
                    softwareSystem);

            Project networkProject = new Project("network project",
                    Timestamp.valueOf("2020-1-18 23:59:59.0"),
                    Timestamp.valueOf("2020-1-4 23:59:59.0"),
                    networkSystem);

            projectService.addNewProject(programmingProject);
            projectService.addNewProject(designProject);
            projectService.addNewProject(networkProject);

            CourseGroup group1SS = new CourseGroup("group 1", softwareSystem);
            CourseGroup group2SS = new CourseGroup("group 2", softwareSystem);
            CourseGroup group3SS = new CourseGroup("group 3", softwareSystem);

            CourseGroup group1NS = new CourseGroup("group 1", networkSystem);
            CourseGroup group2NS = new CourseGroup("group 2", networkSystem);
            CourseGroup group3NS = new CourseGroup("group 3", networkSystem);

            courseGroupService.addCourseGroup(group1SS);
            courseGroupService.addCourseGroup(group2SS);
            courseGroupService.addCourseGroup(group3SS);
            courseGroupService.addCourseGroup(group1NS);
            courseGroupService.addCourseGroup(group2NS);
            courseGroupService.addCourseGroup(group3NS);
            courseGroupService.addCourseGroup(group3NS); //should not leave duplicate

            groupParticipantService.addGroupParticipant(
                    studentService.findStudentByAccountAndCourse(Dylan, softwareSystem), group1SS);
            groupParticipantService.addGroupParticipant(
                    studentService.findStudentByAccountAndCourse(Robin, softwareSystem), group1SS);
            groupParticipantService.addGroupParticipant(
                    studentService.findStudentByAccountAndCourse(Adrijus, softwareSystem), group3SS);
            groupParticipantService.addGroupParticipant(
                    studentService.findStudentByAccountAndCourse(Adrijus, networkSystem), group2NS);
            groupParticipantService.addGroupParticipant(
                    studentService.findStudentByAccountAndCourse(Yevhen, networkSystem), group2NS);
            groupParticipantService.addGroupParticipant(
                    studentService.findStudentByAccountAndCourse(Robin, networkSystem), group1NS);

            submissionService.addNewSubmissionWithSubmissionDate(programmingProject, group1SS, Timestamp.valueOf("2020-1-18 23:59:59.0"));
            submissionService.addNewSubmissionWithSubmissionDate(designProject, group3SS, Timestamp.valueOf("2020-1-28 23:59:59.0"));
            submissionService.addNewSubmissionWithSubmissionDate(networkProject, group2NS, Timestamp.valueOf("2020-1-28 23:59:59.0"));

            Rubric rubricSS_PP = new Rubric(programmingProject);
            Rubric rubricSS_DP = new Rubric(designProject);

            rubricService.addNewRubrics(rubricSS_DP);
            rubricService.addNewRubrics(rubricSS_PP);

            RubricVersion rubricVersionSS_PP_V1 = new RubricVersion(
                    "Programming project rubric",
                    Timestamp.valueOf("2020-2-19 15:17:59.0"),
                    Triet.getName(),
                    0L,
                    rubricSS_PP);

            RubricVersion rubricVersionSS_DP_V1 = new RubricVersion(
                    "Design project rubric",
                    Timestamp.valueOf("2020-2-19 15:17:59.0"),
                    Omer.getName(),
                    0L,
                    rubricSS_DP);

            rubricVersionService.addNewRubricVersion(rubricVersionSS_PP_V1);
            rubricVersionService.addNewRubricVersion(rubricVersionSS_DP_V1);

            Block blockSS_PP_1 = new Block(rubricSS_PP);
            BlockVersion blockVersion_PP_1 = new BlockVersion("CRUCIAL REQUIREMENTS",
                    Timestamp.valueOf("2020-2-19 15:17:59.0"),
                    Triet.getName(),
                    0L,
                    blockSS_PP_1);


            Block blockSS_PP_2 = new Block(rubricSS_PP);
            BlockVersion blockVersion_PP_2 = new BlockVersion("IMPORTANT REQUIREMENTS",
                    Timestamp.valueOf("2020-2-19 16:17:59.0"),
                    Triet.getName(),
                    0L,
                    blockSS_PP_2);

            blockService.addNewBlock(blockSS_PP_1);
            blockService.addNewBlock(blockSS_PP_2);

            blockVersionService.addNewBlockVersion(blockVersion_PP_1);
            blockVersionService.addNewBlockVersion(blockVersion_PP_2);

            Criterion criterionSS_PP_B1_1 = new Criterion(blockSS_PP_1);
            criterionService.addNewCriterion(criterionSS_PP_B1_1);
            CriterionVersion criterionVersionSS_PP_B1_1 = new CriterionVersion("Works using reference server/own client as well as own server/reference client",
                    Timestamp.valueOf("2020-2-19 16:17:59.0"),
                    Triet.getName(),
                    0L,
                    criterionSS_PP_B1_1);
            criterionVersionService.addNewCriterionVersion(criterionVersionSS_PP_B1_1);


            Criterion criterionSS_PP_B1_2 = new Criterion(blockSS_PP_1);
            criterionService.addNewCriterion(criterionSS_PP_B1_2);
            CriterionVersion criterionVersionSS_PP_B1_2 = new CriterionVersion("Has human player",
                    Timestamp.valueOf("2020-2-19 16:17:59.0"),
                    Triet.getName(),
                    0L,
                    criterionSS_PP_B1_2);
            criterionVersionService.addNewCriterionVersion(criterionVersionSS_PP_B1_2);


            Criterion criterionSS_PP_B1_3 = new Criterion(blockSS_PP_1);
            criterionService.addNewCriterion(criterionSS_PP_B1_3);
            CriterionVersion criterionVersionSS_PP_B1_3 = new CriterionVersion("Has computer player",
                    Timestamp.valueOf("2020-2-19 16:17:59.0"),
                    Triet.getName(),
                    0L,
                    criterionSS_PP_B1_3);
            criterionVersionService.addNewCriterionVersion(criterionVersionSS_PP_B1_3);


            Criterion criterionSS_PP_B2_1 = new Criterion(blockSS_PP_2);
            criterionService.addNewCriterion(criterionSS_PP_B2_1);
            CriterionVersion criterionVersionSS_PP_B2_1 = new CriterionVersion("Ask user for port on server start. If port is unavailable ask again",
                    Timestamp.valueOf("2020-2-19 16:17:59.0"),
                    Triet.getName(),
                    0L,
                    criterionSS_PP_B2_1);
            criterionVersionService.addNewCriterionVersion(criterionVersionSS_PP_B2_1);


            Criterion criterionSS_PP_B2_2 = new Criterion(blockSS_PP_2);
            criterionService.addNewCriterion(criterionSS_PP_B2_2);
            CriterionVersion criterionVersionSS_PP_B2_2 = new CriterionVersion("Ask for IP-address and port on client start",
                    Timestamp.valueOf("2020-2-19 16:17:59.0"),
                    Triet.getName(),
                    0L,
                    criterionSS_PP_B2_2);
            criterionVersionService.addNewCriterionVersion(criterionVersionSS_PP_B2_2);


            Criterion criterionSS_PP_B2_3 = new Criterion(blockSS_PP_2);
            criterionService.addNewCriterion(criterionSS_PP_B2_3);
            CriterionVersion criterionVersionSS_PP_B2_3 = new CriterionVersion("Ask for hint as human player",
                    Timestamp.valueOf("2020-2-19 16:17:59.0"),
                    Triet.getName(),
                    0L,
                    criterionSS_PP_B2_3);
            criterionVersionService.addNewCriterionVersion(criterionVersionSS_PP_B2_3);
            CriterionVersion criterionVersionSS_PP_B2_3_V2 = new CriterionVersion("Ask for hint as human player hahaha",
                    Timestamp.valueOf("2020-2-19 19:17:59.0"),
                    Triet.getName(),
                    0L,
                    criterionSS_PP_B2_3);
            criterionVersionService.addNewCriterionVersion(criterionVersionSS_PP_B2_3_V2);

        };
    }

}
