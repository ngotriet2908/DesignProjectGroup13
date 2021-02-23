package com.group13.tcsprojectgrading.service.project;

import com.group13.tcsprojectgrading.model.project.CourseGroup;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.model.user.GroupParticipant;
import com.group13.tcsprojectgrading.model.user.Student;
import com.group13.tcsprojectgrading.repository.project.CourseGroupRepository;
import com.group13.tcsprojectgrading.repository.user.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.group13.tcsprojectgrading.utils.DefaultCanvasUrls.SINGLE_GROUP_MARK;

@Service
public class CourseGroupService {
    private CourseGroupRepository repository;

    public CourseGroupService(CourseGroupRepository repository) {
        this.repository = repository;
    }

    public List<CourseGroup> getCourseGroups() {
        return repository.findAll();
    }

    public CourseGroup addCourseGroup(CourseGroup courseGroup) {
        return repository.save(courseGroup);
    }

    public CourseGroup findSingleCourseGroup(Student student) {
        return repository.findCourseGroupByNameContainingAndCourseGroupCategory_Id(
                student.getId().getAccountId(),
                SINGLE_GROUP_MARK + student.getCourse().getId()).orElse(null);
    }

    public CourseGroup findCanvasGroup(String canvas_id) {
        return repository.findCourseGroupByCanvasId(canvas_id).orElse(null);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public void addAllCourseGroups(List<CourseGroup> courseGroups) {
        repository.saveAll(courseGroups);
    }
}
