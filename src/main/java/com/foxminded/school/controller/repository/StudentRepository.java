package com.foxminded.school.controller.repository;

import java.sql.SQLException;
import java.util.List;

import com.foxminded.school.controller.dao.CourseDao;
import com.foxminded.school.controller.dao.StudentDao;
import com.foxminded.school.model.Course;
import com.foxminded.school.model.Student;

public class StudentRepository {
    private StudentDao studentDao;
    private CourseDao courseDao;
    
    public StudentRepository() {
        this.studentDao = new StudentDao();
        this.courseDao = new CourseDao();
    }
    
    public StudentRepository(StudentDao studentDao, CourseDao courseDao) {
        this.studentDao = studentDao;
        this.courseDao = courseDao;
    }
    
    public int[] add(String firstName, String lastName) throws SQLException {
        return studentDao.create(new Student(firstName, lastName));
    }
    
    public Student getById(int studentId) throws SQLException {
        Student student = studentDao.getPartialById(studentId);
        List<Course> courses = courseDao.getByStudent(student);
        student.setCourses(courses);
        return student;
    }

    public int delete(Student student) throws SQLException {
        return studentDao.delete(student);
    }

    public int addStudentsCourse(Student student, Course course) throws SQLException {
        return studentDao.addStudentsCourse(student, course);
    }

    public int deleteStudentsCourse(Student student, Course course) throws SQLException {
        return studentDao.deleteStudentsCourse(student, course);
    }
}
