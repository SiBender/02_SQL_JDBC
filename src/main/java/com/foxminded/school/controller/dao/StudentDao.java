package com.foxminded.school.controller.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.foxminded.school.controller.db.HikariConnectionPool;
import com.foxminded.school.model.Course;
import com.foxminded.school.model.Student;

public class StudentDao {
    private static final HikariConnectionPool connectionPool = HikariConnectionPool.INSTANCE;
    
    public int[] create(Student student) throws SQLException {
        return create(Arrays.asList(student));
    }
    
    public int[] create(List<Student> students) throws SQLException {
        String query = "INSERT INTO students (first_name, last_name, group_id) VALUES (?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            for (Student student : students) {
                statement.setString(1, student.getFirstName());
                statement.setString(2, student.getLastName());
                if (student.getGroup() > 0 ) {
                    statement.setInt(3, student.getGroup());
                } else {
                    statement.setNull(3, Types.INTEGER);
                }
                statement.addBatch();
            }
            return statement.executeBatch();
        } catch (SQLException e) {
            throw new SQLException("SQL Exception while inserting data into 'students' " + e.getMessage());
        }
    } 
    
    public Student getPartialById(int studentId) throws SQLException {
        String query = "SELECT student_id, first_name, last_name FROM students "
                     + "WHERE student_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);
            ResultSet result = statement.executeQuery();
            result.next();
            Student student = new Student(result.getString(2), result.getString(3));
            student.setId(result.getInt(1));
            return student;
        } catch (SQLException ex) {
            throw new SQLException("SQL error reqesting data about student by ID " + studentId + " " + ex.getMessage());
        }
    }

    public void assignCourses(List<Student> students) throws SQLException {
        String query = "INSERT INTO students_courses (student_id, course_id) VALUES (?, ?)";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            for (Student student : students) {
                for(Course course : student.getCourses()) {
                    statement.setInt(1, student.getId());
                    statement.setInt(2, course.getId());
                    statement.addBatch();
                }
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new SQLException("SQL Exception while inserting data into 'students_courses'" + e.getMessage());
        }       
    }

    public List<Student> getByCourse(Course course) throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT st.student_id, st.first_name, st.last_name FROM students_courses sc " + System.lineSeparator()
                     + "JOIN students st ON st.student_id = sc.student_id" + System.lineSeparator()
                     + "WHERE sc.course_id = ? ORDER BY st.first_name";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, course.getId());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Student student = new Student(result.getString(2), result.getString(3));
                student.setId(result.getInt(1));
                students.add(student);
            }
            return students;
        } catch (SQLException ex) {
            throw new SQLException("SQL error while getting list of students by course");
        }
    } 

    public int delete(Student student) throws SQLException {
        String query = "DELETE FROM students WHERE student_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, student.getId());
            return statement.executeUpdate();
        } catch (SQLException ex) {
            throw new SQLException("SQL error while deleting student");
        }
    }

    public int addStudentsCourse(Student student, Course course) throws SQLException {
        String insertQuery = "INSERT INTO students_courses (student_id, course_id) VALUES (?, ?)";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setInt(1, student.getId());
            statement.setInt(2, course.getId());
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("SQL Exception while adding course to student: " + e.getMessage());
        }
    }

    public int deleteStudentsCourse(Student student, Course course) throws SQLException {
        String query = "DELETE FROM students_courses WHERE student_id = ? AND course_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, student.getId());
            statement.setInt(2, course.getId());
            return statement.executeUpdate();
        } catch (SQLException ex) {
            throw new SQLException("SQL error while deleting student from the course");
        }
    }  
}
