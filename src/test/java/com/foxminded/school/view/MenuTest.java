package com.foxminded.school.view;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import com.foxminded.school.controller.SchoolManager;

import org.apache.commons.lang3.tuple.Pair;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MenuTest {
    private static final String NEW_LINE = System.lineSeparator();
    private static final String EXIT_TEXT = "8";
    
    PrintStream stdOut;
    InputStream stdIn;
    
    @BeforeAll
    void init() throws FileNotFoundException {
        stdOut = System.out;
        stdIn = System.in;
        
        ByteArrayOutputStream outputStubb = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStubb));
    }
    
    @Test
    void startShouldWriteMenuTextToStandartOutputTest() throws IOException {
        String expected = "Please select a menu item number : " + NEW_LINE + NEW_LINE
                        + "1 - Find all groups with less or equals student count" + NEW_LINE
                        + "2 - Find all students related to course with given name" + NEW_LINE
                        + "3 - Add new student" + NEW_LINE
                        + "4 - Delete student by STUDENT_ID" + NEW_LINE
                        + "5 - Add a student to the course (from a list)" + NEW_LINE
                        + "6 - Remove the student from one of his or her courses" + NEW_LINE
                        + "7 - Show courses list" + NEW_LINE
                        + "8 - Exit";
        
        SchoolManager schoolManager = Mockito.mock(SchoolManager.class);
        Mockito.when(schoolManager.askStringUserInput()).thenReturn(EXIT_TEXT);
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        
        Menu menu = new Menu(schoolManager);
        menu.start();
        assertTrue(output.toString().contains(expected));
    }
    
    @Test
    void executeMenuItemShouldCallMethodgetGroupsByMaxNumberOfMembersTest() throws IOException, SQLException {
        String menuItem = "1";
        int groupSize = 20;
        String groupsList = "Group1" + NEW_LINE
                          + "Group2" + NEW_LINE
                          + "Group3";
        SchoolManager schoolManager = Mockito.mock(SchoolManager.class);
        Mockito.when(schoolManager.askStringUserInput()).thenReturn(menuItem);
        Mockito.when(schoolManager.askIntUserInput()).thenReturn(groupSize);
        Mockito.when(schoolManager.getGroupsByMaxNumberOfMembers(groupSize)).thenReturn(groupsList);
        
        Menu menu = new Menu(schoolManager);
        Pair<String, Boolean> output = menu.executeMenuItem();
        assertEquals(groupsList, output.getKey());
    }
    
    @Test
    void executeMenuItemShouldCallMethodGetStudentsByCourseIdTest() throws SQLException, IOException {
        String menuItem = "2";
        int courseId = 1;
        String studentsList = "Name1 Last1" + NEW_LINE
                            + "Name2 Last2" + NEW_LINE
                            + "Name3 Last3" + NEW_LINE;
        SchoolManager schoolManager = Mockito.mock(SchoolManager.class);
        Mockito.when(schoolManager.askStringUserInput()).thenReturn(menuItem);
        Mockito.when(schoolManager.askIntUserInput()).thenReturn(courseId);
        Mockito.when(schoolManager.getStudentsByCourseId(courseId)).thenReturn(studentsList);
        
        Menu menu = new Menu(schoolManager);
        Pair<String, Boolean> output = menu.executeMenuItem();
        assertEquals(studentsList, output.getKey());
    }
    
    @Test
    void executeMenuItemShouldCallMethodAddNewStudentTest() throws SQLException, IOException {
        String menuItem = "3";
        String name = "Name";
        String lastName = "LastName";
        
        SchoolManager schoolManager = Mockito.mock(SchoolManager.class);
        Mockito.when(schoolManager.askStringUserInput()).thenReturn(menuItem, name, lastName);
        Mockito.when(schoolManager.addNewStudent(name, lastName)).thenReturn(true);
        
        Menu menu = new Menu(schoolManager);
        Pair<String, Boolean> output = menu.executeMenuItem();
        
        String expected = "Student added to the database";
        assertEquals(expected, output.getKey());
    }
    
    @Test
    void executeMenuItemShouldCallMethodDeleteStudentByIdTest() throws SQLException, IOException {
        String menuItem = "4";
        int studentId = 10;
        
        SchoolManager schoolManager = Mockito.mock(SchoolManager.class);
        Mockito.when(schoolManager.askStringUserInput()).thenReturn(menuItem);
        Mockito.when(schoolManager.askIntUserInput()).thenReturn(studentId);
        Mockito.when(schoolManager.deleteStudentById(studentId)).thenReturn(true);
        
        Menu menu = new Menu(schoolManager);
        Pair<String, Boolean> output = menu.executeMenuItem();
        
        String expected = "Student removed from database";
        assertEquals(expected, output.getKey());
    }
    
    @Test
    void executeMenuItemShouldCallMethodAddStudentToCourseTest()  throws SQLException, IOException {
        String menuItem = "5";
        int studentId = 100;
        int courseId = 10;
        
        SchoolManager schoolManager = Mockito.mock(SchoolManager.class);
        Mockito.when(schoolManager.askStringUserInput()).thenReturn(menuItem);
        Mockito.when(schoolManager.askIntUserInput()).thenReturn(studentId, courseId);
        Mockito.when(schoolManager.addStudentToCourse(studentId, courseId)).thenReturn(true);
        
        Menu menu = new Menu(schoolManager);
        Pair<String, Boolean> output = menu.executeMenuItem();
        
        String expected = "Student  added to selected course";
        assertEquals(expected, output.getKey());
    }
    
    @Test
    void executeMenuItemShouldCallMethodRemoveStudentFromCourseTest() throws SQLException, IOException {
        String menuItem = "6";
        int studentId = 1000;
        int courseId = 1000;
        
        SchoolManager schoolManager = Mockito.mock(SchoolManager.class);
        Mockito.when(schoolManager.askStringUserInput()).thenReturn(menuItem);
        Mockito.when(schoolManager.askIntUserInput()).thenReturn(studentId, courseId);
        Mockito.when(schoolManager.removeStudentFromCourse(studentId, courseId)).thenReturn(true);
        
        Menu menu = new Menu(schoolManager);
        Pair<String, Boolean> output = menu.executeMenuItem();
        
        String expected = "Student removed from selected course";
        assertEquals(expected, output.getKey());
    }
    
    @Test
    void executeMenuItemShouldCallMethodGetAllCoursesTest() throws SQLException, IOException {
        String menuItem = "7";
        String coursesList = "course 1" + NEW_LINE
                           + "course 2" + NEW_LINE
                           + "course 3";
        
        SchoolManager schoolManager = Mockito.mock(SchoolManager.class);
        Mockito.when(schoolManager.askStringUserInput()).thenReturn(menuItem);
        Mockito.when(schoolManager.getAllCourses()).thenReturn(coursesList);
        
        Menu menu = new Menu(schoolManager);
        Pair<String, Boolean> output = menu.executeMenuItem();

        assertEquals(coursesList, output.getKey());
    }
    
    @Test
    void executeMenuItemShouldReturmErrorMessageWhenExceptionThrownByManagerTest() throws SQLException, IOException {
        String menuItem = "1";
        int crashingGroupSize = -1;
        String SQLExceptionMessage = "Some SQL Exception";
        
        SchoolManager schoolManager = Mockito.mock(SchoolManager.class);
        Mockito.when(schoolManager.askStringUserInput()).thenReturn(menuItem);
        Mockito.when(schoolManager.askIntUserInput()).thenReturn(crashingGroupSize);
        Mockito.when(schoolManager.getGroupsByMaxNumberOfMembers(crashingGroupSize))
               .thenThrow(new SQLException(SQLExceptionMessage), new NumberFormatException(),
                          new RuntimeException());
        
        Menu menu = new Menu(schoolManager);
        Pair<String, Boolean> output = menu.executeMenuItem();
        
        String expected = SQLExceptionMessage;
        assertEquals(expected, output.getKey());
        
        output = menu.executeMenuItem();
        expected = "Input is not a number";
        assertEquals(expected, output.getKey());
        
        output = menu.executeMenuItem();
        expected = "Unexpected error";
        assertEquals(expected, output.getKey());
    }
    
    @AfterAll
    void restore() {
        System.setIn(stdIn);
        System.setOut(stdOut);
    }
}
