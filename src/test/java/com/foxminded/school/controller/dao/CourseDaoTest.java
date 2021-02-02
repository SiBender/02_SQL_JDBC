package com.foxminded.school.controller.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import com.foxminded.school.controller.dao.CourseDao;
import com.foxminded.school.controller.db.HikariConnectionPool;
import com.foxminded.school.controller.db.SQLScriptRunner;
import com.foxminded.school.model.Course;
import com.foxminded.school.model.Student;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CourseDaoTest {
    private static final HikariConnectionPool connectionPool = HikariConnectionPool.INSTANCE;
    private CourseDao courseDao = new CourseDao();
    
    @BeforeEach
    void refresh() throws IOException, SQLException {
        new SQLScriptRunner().init();
    }
    
    @Order(1)
    @Test
    void createShouldAddNewRawInTableCoursesTest() throws SQLException {
        int expectedRowsNum = 6;
        int expectedCourseId = 6;
        String expectedCourseName = "Course 6";
        
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(expectedCourseId, expectedCourseName));
        courseDao.create(courses);
        
        String query = "SELECT COUNT(*) FROM courses";
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            assertEquals(expectedRowsNum, result.getInt(1));
        } 
        
        query = "SELECT course_id, course_name FROM courses where course_id = " + expectedCourseId;
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            assertEquals(expectedCourseId, result.getInt(1));
            assertEquals(expectedCourseName, result.getString(2));
        }
    }
    
    @Order(2)
    @Test
    void createShouldThrowExceptionWhenCourseNameIsNullTest() throws SQLException {        
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(0, null));
        
        Throwable thrown = assertThrows(SQLException.class, () -> {
            courseDao.create(courses);
        });
        
        String expected = "SQL Exception while inserting data into 'courses'";
        assertTrue(thrown.getMessage().contains(expected));
    }

    @Order(3)
    @Test
    void getAllShouldReturnArrayOfCorseObjuctsTest() throws SQLException {
        int expectetArraySize = 5;
        int lastCourseId = 5;
        String lastCourseName = "course5";
        
        List<Course> allCourses = courseDao.getAll();
        
        assertEquals(expectetArraySize, allCourses.size());
        assertEquals(lastCourseId, allCourses.get(allCourses.size() - 1).getId());
        assertEquals(lastCourseName, allCourses.get(allCourses.size() - 1).getName());
    }

    @Order(4)
    @Test
    void getPartialByIdShouldReturnCourseObjectWithEmptyStudentsListTest() throws SQLException {
        int courseId = 1;
        Course course = courseDao.getPartialById(courseId);
        assertEquals(courseId, course.getId());
        assertTrue(course.getStudents() == null);
    }
    
    @Order(5)
    @Test
    void getPartialByIdShouldThrowExceptionIfIdDoesNotExistTest() {
        int courseId = 6;
        
        Throwable thrown = assertThrows(SQLException.class, () -> {
            courseDao.getPartialById(courseId);
        });
        
        String expected = "No course with given ID";
        assertTrue(thrown.getMessage().contains(expected));
    }
    
    @Order(6)
    @Test
    void getByStudentShouldReturnListOfCoursesTest() throws SQLException {
        Student student = new Student("Name", "LastName");
        student.setId(1);
        
        List<Course> studentsCourses = courseDao.getByStudent(student);
        int expectedListSize = 3;
        int expectedCourseId = 1;
        assertEquals(expectedListSize, studentsCourses.size());
        assertEquals(expectedCourseId, studentsCourses.get(0).getId());
    }
    
    @Order(7)
    @Test
    void deleteStudentByIdShouldRemoveRowFromStudentsCoursesTableTest() throws SQLException {
        Course course = new Course(1, "Course1");
        int studentId = 1;
        courseDao.deleteStudentById(course, studentId);
        
        String query = "SELECT COUNT(*) FROM students_courses";
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            int expectedRowsNum = 30;
            assertEquals(expectedRowsNum, result.getInt(1));
        } 
    }
}
