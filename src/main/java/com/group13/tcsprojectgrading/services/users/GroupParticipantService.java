package com.group13.tcsprojectgrading.services.users;

import com.group13.tcsprojectgrading.models.project.CourseGroup;
import com.group13.tcsprojectgrading.models.user.GroupParticipant;
import com.group13.tcsprojectgrading.models.user.Student;
import com.group13.tcsprojectgrading.repositories.user.GroupParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupParticipantService {
    private GroupParticipantRepository repository;

    @Autowired
    public GroupParticipantService(GroupParticipantRepository repository) {
        this.repository = repository;
    }

    public List<GroupParticipant> getGroupParticipants() {
        return repository.findAll();
    }

    public void addGroupParticipant(Student student, CourseGroup courseGroup, Long canvas_id) {
        if (!student.getCourse().equals(courseGroup.getCourseGroupCategory().getCourse())) {
            throw new IllegalArgumentException("Course from student and group are not the same " + student + ";" + courseGroup);
        }
        repository.save(new GroupParticipant(student, courseGroup, canvas_id));
    }

    public void addSingleGroupParticipant(Student student, CourseGroup courseGroup) {
        if (!student.getCourse().equals(courseGroup.getCourseGroupCategory().getCourse())) {
            throw new IllegalArgumentException("Course from student and group are not the same " + student + ";" + courseGroup);
        }
        repository.save(new GroupParticipant(student, courseGroup));
    }
}
