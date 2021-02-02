package com.foxminded.school.controller.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.foxminded.school.controller.db.HikariConnectionPool;
import com.foxminded.school.model.Course;
import com.foxminded.school.model.Student;

public class CourseDao {
    private static final HikariConnectionPool connectionPool = HikariConnectionPool.INSTANCE;
    
    public void create(List<Course> courses) throws SQLException {
        String query = "INSERT INTO courses (course_name, course_description) VALUES (?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            for (Course course : courses) {
                statement.setString(1, course.getName());
                statement.setString(2, course.getDescription());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new SQLException("SQL Exception while inserting data into 'courses' "  + e.getMessage());
        }  
    } 
    
    public List<Course> getAll() throws SQLException {
        String query = "SELECT course_id, course_name FROM courses";
        
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            List<Course> courses = new ArrayList<>();
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                Course course = new Course(result.getInt(1), result.getString(2));
                courses.add(course);
            }
            return courses;
        } catch (SQLException e) {
            throw new SQLException("SQL Exception while getting data from 'courses' " + e.getMessage());
        }
    }
    
    public Course getPartialById(int courseId) throws SQLException {
        String query = "SELECT course_id, course_name FROM courses WHERE course_id = ?";
        
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new Course(result.getInt(1), result.getString(2));
            } else {
                throw new SQLException("No course with given ID");
            }    
        } catch (SQLException e) {
            throw new SQLException("SQL Exception while getting data about course from 'courses' " + e.getMessage());
        }
    }

    public void deleteStudentById(Course course, int studentId) throws SQLException {
        String query = "DELETE FROM students_courses WHERE student_id = ? AND course_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);
            statement.setInt(2, course.getId());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new SQLException("SQL error while deletig student from course");
        }
    }

    public List<Course> getByStudent(Student student) throws SQLException {
        String query = "SELECT crs.course_id, crs.course_name FROM courses crs "
                     + "JOIN students_courses sc ON crs.course_id = sc.course_id " 
                     + "WHERE sc.student_id = ? ORDER BY 1";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, student.getId());
            ResultSet result = statement.executeQuery();
            List<Course> courses = new ArrayList<>();
            while (result.next()) {
                Course course = new Course(result.getInt(1), result.getString(2));
                courses.add(course);
            }
            return courses;
        } catch (SQLException ex) {
            throw new SQLException("SQL error reqesting data about students courses by student");
        } 
    }
}
