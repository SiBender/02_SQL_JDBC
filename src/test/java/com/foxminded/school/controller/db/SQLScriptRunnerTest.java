package com.foxminded.school.controller.db;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SQLScriptRunnerTest {
    private final HikariConnectionPool connectionPool = HikariConnectionPool.INSTANCE;
    
    @BeforeAll
    void init() throws IOException, SQLException {
        new SQLScriptRunner().init();
    }
    
    @Test
    void initShouldCreateTableGroupsWith5RowsTest() throws SQLException {
        int expectedGroupsNum = 5;
        
        String query = "SELECT COUNT(*) FROM groups";

        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            assertEquals(expectedGroupsNum, result.getInt(1));
        }
    }

    @Test
    void initShouldCreateTableStudentsWith20RowsTest() throws SQLException {
        int expectedStudenstsNum = 20;
        
        String query = "SELECT COUNT(*) FROM students";

        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            assertEquals(expectedStudenstsNum, result.getInt(1));
        }
    }
    
    @Test
    void initShouldCreateTableCoursesWith5RowsTest() throws SQLException {
        int expectedCoursesNum = 5;
        
        String query = "SELECT COUNT(*) FROM courses";

        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            assertEquals(expectedCoursesNum, result.getInt(1));
        } 
    }
    
    @Test
    void initShouldCreateTableStudentsCoursesWith31RowsTest() throws SQLException {
        int expectedRowsNum = 31;
        
        String query = "SELECT COUNT(*) FROM students_courses";

        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            assertEquals(expectedRowsNum, result.getInt(1));
        }
    }
}
