package com.group13.tcsprojectgrading.service;

import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.model.user.Student;
import com.group13.tcsprojectgrading.model.user.TeachingAssistant;
import com.group13.tcsprojectgrading.repository.user.StudentRepository;
import com.group13.tcsprojectgrading.repository.user.TeachingAssistantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeachingAssistantService {

    private TeachingAssistantRepository teachingAssistantRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AccountService accountService;

    public TeachingAssistantService(TeachingAssistantRepository teachingAssistantRepository) {
        this.teachingAssistantRepository = teachingAssistantRepository;
    }

    public List<TeachingAssistant> getTeachers() {
        return teachingAssistantRepository.findAll();
    }

    public TeachingAssistant addNewTA(Course course, Account account) {
        if (courseService.existsById(course.getId()) &&
                accountService.existsById(account.getId())) {
            TeachingAssistant teachingAssistant = new TeachingAssistant(account, course);
            teachingAssistantRepository.save(teachingAssistant);
            return teachingAssistant;
        }
        return null;
    }
}
