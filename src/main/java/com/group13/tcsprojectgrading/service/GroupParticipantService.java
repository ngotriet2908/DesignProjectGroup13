package com.group13.tcsprojectgrading.service;

import com.group13.tcsprojectgrading.model.project.CourseGroup;
import com.group13.tcsprojectgrading.model.user.GroupParticipant;
import com.group13.tcsprojectgrading.model.user.Student;
import com.group13.tcsprojectgrading.repository.project.CourseGroupRepository;
import com.group13.tcsprojectgrading.repository.user.GroupParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupParticipantService {
    private GroupParticipantRepository repository;

    @Autowired
    private CourseGroupService courseGroupService;

    @Autowired
    private StudentService studentService;

    @Autowired
    public GroupParticipantService(GroupParticipantRepository repository) {
        this.repository = repository;
    }

    public List<GroupParticipant> getGroupParticipants() {
        return repository.findAll();
    }

    public void addGroupParticipant(Student student, CourseGroup courseGroup) {
        if (!student.getCourse().equals(courseGroup.getCourse())) {
            throw new IllegalArgumentException("Course from student and group are not the same " + student + ";" + courseGroup);
        }
        repository.save(new GroupParticipant(student, courseGroup));
    }
}
