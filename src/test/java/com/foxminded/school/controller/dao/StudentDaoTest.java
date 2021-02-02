package com.foxminded.school.controller.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import com.foxminded.school.controller.db.HikariConnectionPool;
import com.foxminded.school.controller.db.SQLScriptRunner;
import com.foxminded.school.model.Course;
import com.foxminded.school.model.Student;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentDaoTest {
    private final HikariConnectionPool connectionPool = HikariConnectionPool.INSTANCE;
    private StudentDao studentDao = new StudentDao();
    
    @BeforeAll
    void refresh() throws IOException, SQLException {
        new SQLScriptRunner().init();
    }
    
    @Order(1)
    @Test
    void createStudentShouldInsertNewRowToStudentsTableTest() throws SQLException {
        String name = "name";
        String lastName = "lastName";
        int expectedStudentId = 21;
        Student student = new Student(name, lastName);
        studentDao.create(student);
        
        String query = "SELECT COUNT(*) FROM students";
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            int expectedRowsNum = 21;
            assertEquals(expectedRowsNum, result.getInt(1));
        } 
        
        query = "SELECT student_id, first_name, last_name FROM students ORDER BY student_id DESC LIMIT 1";
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            assertEquals(expectedStudentId, result.getInt(1));
            assertEquals(name, result.getString(2));
            assertEquals(lastName, result.getString(3));
        } 
    }
    
    @Order(2)
    @Test
    void createStudentShouldThrowExceptionIfStudentsNameIsNullTest() throws SQLException {
        Student student = new Student(null, "");
        
        Throwable thrown = assertThrows(SQLException.class, () -> {
            studentDao.create(student);
        });
        
        String expected = "SQL Exception while inserting data into 'students'";
        assertTrue(thrown.getMessage().contains(expected));
    }

    @Order(3)
    @Test
    void createListOfStudentShouldInsertMultipleRowsToStudentsTable() throws SQLException {
        List<Student> students = new ArrayList<>();
        students.add(new Student("name1", "lastName1"));
        students.add(new Student("name2", "lastName2"));
        students.add(new Student("name3", "lastName3"));
        students.add(new Student("name4", "lastName4"));
        students.add(new Student("name5", "lastName5"));
        
        studentDao.create(students);
        
        String query = "SELECT COUNT(*) FROM students";
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            int expectedRowsNum = 26;
            assertEquals(expectedRowsNum, result.getInt(1));
        } 
    }
    
    @Order(4)
    @Test
    void getPartialByIdShouldReturnStudentObjectWithEmptyCoursesListTest() throws SQLException {
        int studentId = 1;
        Student student = studentDao.getPartialById(studentId);
        
        assertEquals(studentId, student.getId());
        assertTrue(student.getCourses() == null);
    }

    @Order(5)
    @Test
    void getByCourseShouldReturnListOfStudentObjectsTest() throws SQLException {
        Course course = new Course(1, "course1");
        List<Student> students = studentDao.getByCourse(course);
        
        int expectedListSize = 7;
        assertEquals(expectedListSize, students.size());
    }
    
    @Order(6)
    @Test
    void assignCoursesShouldInsertMultipleRowsToStudentsCoursesTableTest() throws SQLException {
        Student student = new Student("name", "lastName");
        student.setId(9);
        
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(1, "course1"));
        courses.add(new Course(2, "course2"));
        courses.add(new Course(3, "course3"));
        courses.add(new Course(4, "course4"));
        
        student.setCourses(courses);
        List<Student> students = new ArrayList<>();
        students.add(student);
        
        studentDao.assignCourses(students);
        
        String query = "SELECT COUNT(*) FROM students_courses";
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            int expectedRowsNum = 35;
            assertEquals(expectedRowsNum, result.getInt(1));
        } 
    }
    
    @Order(7)
    @Test
    void assignCoursesShouldThrowExceptionForRepeatedDataTest() throws SQLException {
        Student student = new Student("name", "lastName");
        student.setId(9);
        
        List<Course> courses = new ArrayList<>();
        courses.add(new Course(1, "course1"));
        courses.add(new Course(2, "course2"));
        courses.add(new Course(3, "course3"));
        courses.add(new Course(3, "course3"));
        
        student.setCourses(courses);
        List<Student> students = new ArrayList<>();
        students.add(student);
        
        Throwable thrown = assertThrows(SQLException.class, () -> {
            studentDao.assignCourses(students);
        });
        
        String expected = "SQL Exception while inserting data into 'students_courses'";
        assertTrue(thrown.getMessage().contains(expected)); 
    }

    

    @Order(8)
    @Test
    void addStudentsCourseShouldAddNewRowToStudentsCoursesTableTest() throws SQLException {
        Student student = new Student("Name", "LastName");
        student.setId(1);
        Course course = new Course(4, "course4");
        studentDao.addStudentsCourse(student, course);
        
        String query = "SELECT COUNT(*) FROM students_courses";
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            int expectedRowsNum = 36;
            assertEquals(expectedRowsNum, result.getInt(1));
        } 
    }
    
    @Order(9)
    @Test
    void addStudentsCourseShouldThrowExceptionIfDataInStudentsCoursesTableDuplicatesTest() throws SQLException {
        Student student = new Student("Name", "LastName");
        student.setId(1);
        Course course = new Course(1, "course1");
        
        Throwable thrown = assertThrows(SQLException.class, () -> {
            studentDao.addStudentsCourse(student, course);
        });
        
        String expected = "SQL Exception while adding course to student";
        assertTrue(thrown.getMessage().contains(expected));
    }

    @Order(Integer.MAX_VALUE - 1)
    @Test
    void deleteStudentsCourseShouldRemoveRowFromStudentsCoursesTableTest() throws SQLException {
        Student student = new Student("Name", "LastName");
        student.setId(1);
        Course course = new Course(1, "course4");
        studentDao.deleteStudentsCourse(student, course);
        
        String query = "SELECT COUNT(*) FROM students_courses";
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            int expectedRowsNum = 35;
            assertEquals(expectedRowsNum, result.getInt(1));
        } 
    }
    
    @Order(Integer.MAX_VALUE)
    @Test
    void deleteShouldDeleteRowFromStudentsTableTest() throws SQLException {
        Student student = new Student("Name", "LastName");
        student.setId(1);
        studentDao.delete(student);
        
        String query = "SELECT COUNT(*) FROM students";
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            int expectedRowsNum = 25;
            assertEquals(expectedRowsNum, result.getInt(1));
        } 
    }
}
