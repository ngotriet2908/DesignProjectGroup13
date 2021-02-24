package com.group13.tcsprojectgrading.services.users;

import com.group13.tcsprojectgrading.models.course.Course;
import com.group13.tcsprojectgrading.models.user.Account;
import com.group13.tcsprojectgrading.models.user.Student;
import com.group13.tcsprojectgrading.repositories.user.StudentRepository;
import com.group13.tcsprojectgrading.services.courses.CoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    private CoursesService courseService;

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
                System.out.println(account.getName() + " as Student already exists.");
                return studentRepository.findById(student.getId()).orElse(null);
            } else {
                System.out.println(account.getName() + " as Student does not exist, creating a new student.");
                return studentRepository.save(student);
            }
        }
        return null;
    }

    public Student findStudentByAccountAndCourse(Account account, Course course) {
        return studentRepository.findByAccountAndCourse(account, course);
    }

    public List<Student> findStudentByCourse(Course course) {
        return studentRepository.findStudentByCourse(course);
    }
}
