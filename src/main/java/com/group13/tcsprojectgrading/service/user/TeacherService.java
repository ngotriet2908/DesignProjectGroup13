package com.group13.tcsprojectgrading.service.user;

import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.model.user.Teacher;
import com.group13.tcsprojectgrading.repository.user.TeacherRepository;
import com.group13.tcsprojectgrading.service.course.CourseService;
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
        //TODO should I add account and course creation here ? come back when needed

        if (courseService.existsById(course.getId()) && accountService.existsById(account.getId())) {
            Teacher teacher = new Teacher(account, course);
            if (teacherRepository.existsById(teacher.getId())) {
                System.out.println(account.getName() + " as Teacher is already existed");
                return teacherRepository.findById(teacher.getId()).orElse(null);
            } else {
                System.out.println(account.getName() + " as Teacher is not existed, creating new teacher");
                return teacherRepository.save(teacher);
            }
        }
        return null;
    }

}
