package com.group13.tcsprojectgrading.service;

import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.model.user.Student;
import com.group13.tcsprojectgrading.model.user.Teacher;
import com.group13.tcsprojectgrading.repository.user.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {

    private TeacherRepository teacherRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AccountService accountService;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<Teacher> getTeachers() {
        return teacherRepository.findAll();
    }

    public Teacher addNewTeacher(Course course, Account account) {
        if (courseService.existsById(course.getId()) &&
                accountService.existsById(account.getId())) {
            Teacher teacher = new Teacher(account, course);
            teacherRepository.save(teacher);
            return teacher;
        }
        return null;
    }
}
