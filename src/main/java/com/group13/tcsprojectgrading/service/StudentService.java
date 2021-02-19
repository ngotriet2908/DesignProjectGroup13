package com.group13.tcsprojectgrading.service;

import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.model.user.Participant;
import com.group13.tcsprojectgrading.model.user.Student;
import com.group13.tcsprojectgrading.repository.user.AccountRepository;
import com.group13.tcsprojectgrading.repository.user.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class StudentService {

    private StudentRepository studentRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AccountService accountService;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public Student addNewStudent(Course course, Account account) {
        if (courseService.existsById(course.getId()) &&
            accountService.existsById(account.getId())) {
            Student student = new Student(account, course);
            studentRepository.save(student);
            return student;
        }
        return null;
    }

    public Student findStudentByAccountAndCourse(Account account, Course course) {
        return studentRepository.findByAccountAndCourse(account, course);
    }
}
