package com.foxminded.school.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;

import com.foxminded.school.model.Course;
import com.foxminded.school.model.Group;
import com.foxminded.school.model.Student;
import com.foxminded.school.controller.repository.*;

public class SchoolManager {
    Scanner scanner = new Scanner(System.in);
    
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    
    public SchoolManager() {
        groupRepository = new GroupRepository();
        courseRepository = new CourseRepository();
        studentRepository = new StudentRepository();
    }
    
    public SchoolManager(StudentRepository studentRepository, GroupRepository groupRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.courseRepository = courseRepository;
    }
    
    public String getAllCourses() throws SQLException {
        return buildCoursesList(courseRepository.getAll());
    }
    
    public String getGroupsByMaxNumberOfMembers(int groupSize) throws SQLException{
        return buildGroupsList(groupRepository.getByMaxSize(groupSize));
    }
    
    public String getStudentsByCourseId(int courseId) throws SQLException {
        Course course = courseRepository.getById(courseId);
        return buildStudentsList(course.getStudents());
    }
    
    public boolean addNewStudent(String firstName, String lastName) throws SQLException {
        if (firstName.length() == 0) {throw new SQLException("Students name cannot be empty"); }
        int[] result = studentRepository.add(firstName, lastName);
        return result[0] == 1 ? true : false;
    }
    
    public boolean deleteStudentById(int studentId) throws SQLException {
        Student student = studentRepository.getById(studentId);
        return studentRepository.delete(student) == 1 ? true : false;
    }
    
    public boolean addStudentToCourse(int studentId, int courseId) throws SQLException {
        Student student = studentRepository.getById(studentId);
        Course course = courseRepository.getById(courseId);
        return studentRepository.addStudentsCourse(student, course) == 1 ? true : false;
    }
    
    public boolean removeStudentFromCourse(int studentId, int courseId) throws SQLException {
        Student student = studentRepository.getById(studentId);
        Course course = courseRepository.getById(courseId);
        return studentRepository.deleteStudentsCourse(student, course) == 1 ? true : false;
    }
    
    private String buildStudentsList(List<Student> students) {
        if (students.isEmpty()) { return "No students"; }
        
        StringJoiner output = new StringJoiner(System.lineSeparator());
        for (Student student : students) {
            output.add(String.format("%s %s", student.getFirstName(), student.getLastName()));
        }
        return output.toString();
    }

    private String buildCoursesList(List<Course> courses) {
        if (courses.isEmpty()) { return "Courses list is empty"; }
        StringJoiner output = new StringJoiner(System.lineSeparator());
        for (Course course : courses) {
            output.add(String.format("%-3d| %s", course.getId() , course.getName()));
        }
        return output.toString();
    }
    
    private String buildGroupsList(List<Group> groups) {
        if (groups.isEmpty()) {
            return "No groups matching the condition";
        }
        
        StringJoiner output = new StringJoiner(System.lineSeparator());
        for (Group group : groups) {
            output.add(group.getName());
        }
        return output.toString();
    }

    public int askIntUserInput() throws NumberFormatException {
        return Integer.parseInt(scanner.nextLine().trim());
    }

    public String askStringUserInput() {
        return scanner.nextLine().trim();
    }
}
