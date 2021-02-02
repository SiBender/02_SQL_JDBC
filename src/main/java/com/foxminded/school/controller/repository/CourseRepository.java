package com.foxminded.school.controller.repository;

import java.sql.SQLException;
import java.util.List;

import com.foxminded.school.controller.dao.CourseDao;
import com.foxminded.school.controller.dao.StudentDao;
import com.foxminded.school.model.Course;
import com.foxminded.school.model.Student;

public class CourseRepository {
    private final CourseDao courseDao;
    private final StudentDao studentDao;
    
    public CourseRepository() {
        this.courseDao = new CourseDao();
        this.studentDao = new StudentDao();
    }
    
    public CourseRepository(CourseDao courseDao, StudentDao studentDao) {
        this.courseDao = courseDao;
        this.studentDao = studentDao;
    }
    
    public Course getById(int courseId) throws SQLException {
        Course course = courseDao.getPartialById(courseId);
        List<Student> students = studentDao.getByCourse(course); 
        course.setStudents(students);
        return course;
    }

    public List<Course> getAll() throws SQLException {
        return courseDao.getAll();
    }
}
