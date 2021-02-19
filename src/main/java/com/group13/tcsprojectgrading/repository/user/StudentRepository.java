package com.group13.tcsprojectgrading.repository.user;

import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.model.user.ParticipantKey;
import com.group13.tcsprojectgrading.model.user.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, ParticipantKey> {

    public Student findStudentByAccount(Account account);

    public Student findByAccountAndCourse(Account account, Course course);

}
