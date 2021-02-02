package com.foxminded.school.view;

import java.io.IOException;
import java.sql.SQLException;

import com.foxminded.school.controller.SchoolManager;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Menu {
    private static final String NEW_LINE = System.lineSeparator();
    
    private SchoolManager manager;

    public Menu(SchoolManager schoolManager) {
        this.manager = schoolManager;
    }
    
    public Menu() {
        this.manager = new SchoolManager();
    }
    
    public void start() throws IOException {
        while (true) {
            System.out.println(NEW_LINE + "Please select a menu item number : " + NEW_LINE);
            System.out.println("1 - Find all groups with less or equals student count" + NEW_LINE
                + "2 - Find all students related to course with given name" + NEW_LINE
                + "3 - Add new student" + NEW_LINE
                + "4 - Delete student by STUDENT_ID" + NEW_LINE
                + "5 - Add a student to the course (from a list)" + NEW_LINE
                + "6 - Remove the student from one of his or her courses" + NEW_LINE
                + "7 - Show courses list" + NEW_LINE
                + "8 - Exit" + NEW_LINE);
                
            Pair<String, Boolean> result = executeMenuItem();
            System.out.println(result.getKey());
            
            if (!result.getValue()) {
                break;
            }
        }
    }
    
    public Pair<String, Boolean> executeMenuItem() {
        boolean isContinue = true;
        String message = "";
        try {
            switch (manager.askStringUserInput()) {
            case "1":
                System.out.println(NEW_LINE + "Enter the number of students :");
                int groupSize = manager.askIntUserInput();
                message = manager.getGroupsByMaxNumberOfMembers(groupSize);
                break;
            case "2":
                System.out.println("Enter number of course listed below:");
                System.out.println(manager.getAllCourses());
                int courseId = manager.askIntUserInput();
                message = manager.getStudentsByCourseId(courseId);
                break;
            case "3":
                System.out.println("Enter first name :");
                String firstName = manager.askStringUserInput();
                System.out.println(NEW_LINE + "Enter last name :");
                String lastName = manager.askStringUserInput();
                message = manager.addNewStudent(firstName, lastName)
                    ? "Student added to the database"
                    : "Student not added to database";
                break;
            case "4":
                System.out.println(NEW_LINE + "Enter student id :");
                int studentId = manager.askIntUserInput();
                message = manager.deleteStudentById(studentId)
                    ? "Student removed from database"
                    : "Student not found in database";
                break;
            case "5":
                System.out.println(NEW_LINE + "Enter student id :");
                studentId = manager.askIntUserInput();
                System.out.println(NEW_LINE + "Enter course id :");
                courseId = manager.askIntUserInput();
                message = manager.addStudentToCourse(studentId, courseId)
                    ? "Student  added to selected course"
                    : "Student not added to course";
                break;
            case "6":
                System.out.println(NEW_LINE + "Enter student id :");
                studentId = manager.askIntUserInput();
                System.out.println(NEW_LINE + "Enter course id :");
                courseId = manager.askIntUserInput();
                message = manager.removeStudentFromCourse(studentId, courseId)
                    ? "Student removed from selected course"
                    : "Student not removed from course";
                break;
            case "7":
                System.out.println(NEW_LINE + "List of courses:");
                message = manager.getAllCourses();
                break;
            case "8":
                message = NEW_LINE + "Application stopped";
                isContinue = false;
                break;
            default:
                message = NEW_LINE + "Menu item selection error";
                break;
            }
        } catch (SQLException ex) {
            message = ex.getMessage();
        } catch (NumberFormatException ex) {
            message = "Input is not a number";
        } catch (RuntimeException ex) {
            message = "Unexpected error";
        }
        return new ImmutablePair<>(message, isContinue);
    }
}
