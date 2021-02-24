package com.group13.tcsprojectgrading.repositories.user;

import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.user.Account;
import com.group13.tcsprojectgrading.models.user.ParticipantKey;
import com.group13.tcsprojectgrading.models.user.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, ParticipantKey> {

    public Student findStudentByAccount(Account account);

    public Student findByAccountAndCourse(Account account, Course course);

    public List<Student> findStudentByCourse(Course course);

}
