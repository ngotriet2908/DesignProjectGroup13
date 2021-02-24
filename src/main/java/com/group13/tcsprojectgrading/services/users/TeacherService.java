package com.group13.tcsprojectgrading.services.users;

import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.user.Account;
import com.group13.tcsprojectgrading.models.user.Teacher;
import com.group13.tcsprojectgrading.repositories.user.TeacherRepository;
import com.group13.tcsprojectgrading.services.courses.CoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {
    private final TeacherRepository teacherRepository;

    @Autowired
    private CoursesService coursesService;

    @Autowired
    private AccountService accountService;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<Teacher> getTeachers() {
        return teacherRepository.findAll();
    }

    public Teacher addNewTeacher(Course course, Account account) {
        //TODO should I add account and course creation here ? come back when needed

        if (coursesService.existsById(course.getId()) && accountService.existsById(account.getId())) {
            Teacher teacher = new Teacher(account, course);
            if (teacherRepository.existsById(teacher.getId())) {
                System.out.println(account.getName() + " as Teacher already exists.");
                return teacherRepository.findById(teacher.getId()).orElse(null);
            } else {
                System.out.println(account.getName() + " as Teacher does not exist, creating a new teacher.");
                return teacherRepository.save(teacher);
            }
        }
        return null;
    }
}
