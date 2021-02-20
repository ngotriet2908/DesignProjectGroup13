package com.group13.tcsprojectgrading.service.user;

import com.group13.tcsprojectgrading.model.course.Course;
import com.group13.tcsprojectgrading.model.user.Account;
import com.group13.tcsprojectgrading.model.user.Student;
import com.group13.tcsprojectgrading.repository.user.StudentRepository;
import com.group13.tcsprojectgrading.service.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            if (studentRepository.existsById(student.getId())) {
                System.out.println(account.getName() + " as Student is already existed");
                return studentRepository.findById(student.getId()).orElse(null);
            } else {
                System.out.println(account.getName() + " as Student is not existed, creating new student");
                return studentRepository.save(student);
            }
        }
        return null;
    }

    public Student findStudentByAccountAndCourse(Account account, Course course) {
        return studentRepository.findByAccountAndCourse(account, course);
    }
}
