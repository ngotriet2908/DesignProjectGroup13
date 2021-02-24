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
        if (courseGroup.getCanvasId() == null) {
            CourseGroup courseGroup1 = repository.findCourseGroupByNameAndCourseGroupCategory_Id(courseGroup.getName(), courseGroup.getCourseGroupCategory().getId()).orElse(null);
            if (courseGroup1 == null) {
                System.out.println("group " + courseGroup.getName() + " is not exist, creating new group");
                return repository.save(courseGroup);
            } else {
                System.out.println("group " + courseGroup.getName() + " is exist, updating info group");
                return courseGroup1;
            }
        } else {
            CourseGroup courseGroup1 = repository.findCourseGroupByCanvasId(courseGroup.getCanvasId()).orElse(null);
            if (courseGroup1 == null) {
                System.out.println("group " + courseGroup.getName() + " is not exist, creating new group");
                return repository.save(courseGroup);
            } else {
                courseGroup.setId(courseGroup1.getId());
                System.out.println("group " + courseGroup.getName() + " is exist, updating info group");
                return repository.save(courseGroup);
            }
        }
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
