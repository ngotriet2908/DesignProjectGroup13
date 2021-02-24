package com.group13.tcsprojectgrading.services.projects;

import com.group13.tcsprojectgrading.canvas.api.CanvasEndpoints;
import com.group13.tcsprojectgrading.models.project.CourseGroup;
import com.group13.tcsprojectgrading.models.user.Student;
import com.group13.tcsprojectgrading.repositories.project.CourseGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseGroupService {
    public static final String SINGLE_GROUP_MARK = "-";
    public static final String SINGLE_GROUP_NAME_PREFIX = "Single Group of ";

    private final CourseGroupRepository repository;

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
