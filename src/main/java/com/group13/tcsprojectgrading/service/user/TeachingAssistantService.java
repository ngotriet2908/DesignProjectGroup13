package com.group13.tcsprojectgrading.service.user;

import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.model.user.TeachingAssistant;
import com.group13.tcsprojectgrading.repository.user.TeachingAssistantRepository;
import com.group13.tcsprojectgrading.service.course.CourseService;
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
            if (teachingAssistantRepository.existsById(teachingAssistant.getId())) {
                System.out.println(account.getName() + " as TA is already existed");
                return teachingAssistantRepository.findById(teachingAssistant.getId()).orElse(null);
            } else {
                System.out.println(account.getName() + " as TA  is not existed, creating new TA");
                return teachingAssistantRepository.save(teachingAssistant);
            }
        }
        return null;
    }
}
