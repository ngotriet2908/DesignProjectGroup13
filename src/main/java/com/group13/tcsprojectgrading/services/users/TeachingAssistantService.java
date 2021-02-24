package com.group13.tcsprojectgrading.services.users;

import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.user.Account;
import com.group13.tcsprojectgrading.models.user.TeachingAssistant;
import com.group13.tcsprojectgrading.repositories.user.TeachingAssistantRepository;
import com.group13.tcsprojectgrading.services.courses.CoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeachingAssistantService {

    private final TeachingAssistantRepository teachingAssistantRepository;

    @Autowired
    private CoursesService coursesService;

    @Autowired
    private AccountService accountService;

    public TeachingAssistantService(TeachingAssistantRepository teachingAssistantRepository) {
        this.teachingAssistantRepository = teachingAssistantRepository;
    }

    public List<TeachingAssistant> getTeachers() {
        return teachingAssistantRepository.findAll();
    }

    public TeachingAssistant addNewTA(Course course, Account account) {
        if (coursesService.existsById(course.getId()) &&
                accountService.existsById(account.getId())) {
            TeachingAssistant teachingAssistant = new TeachingAssistant(account, course);
            if (teachingAssistantRepository.existsById(teachingAssistant.getId())) {
                System.out.println(account.getName() + " as TA already exists.");
                return teachingAssistantRepository.findById(teachingAssistant.getId()).orElse(null);
            } else {
                System.out.println(account.getName() + " as TA  does not exist, creating a new TA.");
                return teachingAssistantRepository.save(teachingAssistant);
            }
        }
        return null;
    }
}
