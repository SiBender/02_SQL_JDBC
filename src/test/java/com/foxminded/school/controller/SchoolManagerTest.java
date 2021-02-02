package com.foxminded.school.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import com.foxminded.school.controller.repository.*;
import com.foxminded.school.model.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SchoolManagerTest {
    private static final String NEW_LINE = System.lineSeparator();
    private static final int COURSE_WITH_STUDENTS_ID = 10;
    
    StudentRepository studentRepository;
    GroupRepository groupRepository;
    CourseRepository courseRepository;
    
    SchoolManager schoolManager;
    
    @BeforeAll
    void init() throws SQLException {
        studentRepository = Mockito.mock(StudentRepository.class);
        groupRepository = Mockito.mock(GroupRepository.class);
        courseRepository = Mockito.mock(CourseRepository.class);
        
        schoolManager = new SchoolManager(studentRepository, groupRepository, courseRepository);
        
        List<Course> allCourses = new ArrayList<>();
        allCourses.add(new Course(1, "course 1"));
        allCourses.add(new Course(2, "course 2"));
        allCourses.add(new Course(3, "course 3"));
        allCourses.add(new Course(4, "course 4"));
        allCourses.add(new Course(5, "course 5"));
        
        List<Group> groups = new ArrayList<>();
        groups.add(new Group("group1"));
        groups.add(new Group("group2"));
        groups.add(new Group("group3"));
        groups.add(new Group("group4"));
        groups.add(new Group("group5"));
        
        List<Student> courseStudents = new ArrayList<>();
        courseStudents.add(new Student("aaa", "aaa"));
        courseStudents.add(new Student("bbb", "bbb"));
        courseStudents.add(new Student("ccc", "ccc"));
        Course courseWithStudents = new Course(1, "course with students");
        courseWithStudents.setStudents(courseStudents);
        
        Mockito.when(courseRepository.getAll()).thenReturn(allCourses);
        
        Mockito.when(groupRepository.getByMaxSize(0)).thenReturn(new ArrayList<Group>());
        Mockito.when(groupRepository.getByMaxSize(10)).thenReturn(groups.subList(0, 3));
        Mockito.when(groupRepository.getByMaxSize(20)).thenReturn(groups);
        
        Mockito.when(courseRepository.getById(COURSE_WITH_STUDENTS_ID)).thenReturn(courseWithStudents);
        Mockito.when(courseRepository.getById(Mockito.intThat(id -> id > 0 && id <= 5))).thenReturn(new Course(1, "courseName"));
        
        Mockito.when(studentRepository.add("John", "Smith")).thenReturn(new int[] {1});
        Mockito.when(studentRepository.add("Tom", "Jones")).thenReturn(new int[] {0});
        Mockito.when(studentRepository.add("Bill", "Swat")).thenReturn(new int[] {1});
        
        Mockito.when(studentRepository.getById(Mockito.intThat(id -> id > 0 && id <= 50)))
               .thenReturn(new Student("Name", ""));
        
        Mockito.when(studentRepository.delete(Mockito.any(Student.class))).thenReturn(1);
        Mockito.when(studentRepository.delete(null)).thenReturn(0);
        
        Mockito.when(studentRepository.addStudentsCourse(Mockito.any(Student.class), Mockito.any(Course.class)))
               .thenReturn(1);
        Mockito.when(studentRepository.deleteStudentsCourse(Mockito.any(Student.class), Mockito.any(Course.class)))
        .thenReturn(1);
    }
    
    @Test
    void getAllCoursesShouldReturnStringWithListOfAllCoursesTest() throws SQLException {
        String expected = "1  | course 1" + NEW_LINE 
                        + "2  | course 2" + NEW_LINE
                        + "3  | course 3" + NEW_LINE
                        + "4  | course 4" + NEW_LINE
                        + "5  | course 5";
        assertEquals(expected, schoolManager.getAllCourses());
    }
    
    @Test
    void getGroupsByMaxNumberOfMembersShouldReturnStringWithListOfGroupsTest() throws SQLException{
        String expected = "No groups matching the condition";
        assertEquals(expected, schoolManager.getGroupsByMaxNumberOfMembers(0));
        
        expected = "group1" + NEW_LINE
                 + "group2" + NEW_LINE
                 + "group3";
        assertEquals(expected, schoolManager.getGroupsByMaxNumberOfMembers(10));
        
        expected = "group1" + NEW_LINE
                 + "group2" + NEW_LINE
                 + "group3" + NEW_LINE
                 + "group4" + NEW_LINE
                 + "group5";
       assertEquals(expected, schoolManager.getGroupsByMaxNumberOfMembers(20));
    }
    
    @Test
    void getStudentsByCourseIdShouldReturnStringWithListOfStudents() throws SQLException {
        String expected = "aaa aaa" + NEW_LINE
                        + "bbb bbb" + NEW_LINE 
                        + "ccc ccc";
        assertEquals(expected, schoolManager.getStudentsByCourseId(COURSE_WITH_STUDENTS_ID));
    }
    
    @ParameterizedTest
    @CsvSource({"'John','Smith', true",
                "'Tom','Jones', false",
                "'Bill','Swat', true"})
    void addNewStudentShouldReturnBooleanValueDependsOnNumberOnAffectedRowsTest(String name, String lastName, boolean expected) throws SQLException {
        assertEquals(expected, schoolManager.addNewStudent(name, lastName));
    }
    
    @ParameterizedTest
    @CsvSource({"1, true",
                "45, true",
                "-1000, false",
                "100, false"})
    void deleteStudentByIdShouldReturnBolleanValueDependsOnStudentIdTest(int studentId, boolean expected) throws SQLException {
        assertEquals(expected, schoolManager.deleteStudentById(studentId));
    }
    
    @ParameterizedTest
    @CsvSource({"1, 1",
                "49, 4",
                "10, 5"})
    void addStudentToCourseShouldReturnTrueIfStudentIdLessThan50AndCourseIdLessThan6Test(int studentId, int courseId) throws SQLException {
        assertEquals(true, schoolManager.addStudentToCourse(studentId, courseId));
    }
    
    @ParameterizedTest
    @CsvSource({"-1, 1",
                "51, 4",
                "100, 5",
                "1, 6",
                "10, 9",
                "40, -5",
                "-1, 60",
                "1000, 1000",
                "-100, 500"})
    void addStudentToCourseShouldReturnFalseIfStudentIdMoreThan50OrCourseIdMoreThan5Test(int studentId, int courseId) throws SQLException {
        assertEquals(false, schoolManager.addStudentToCourse(studentId, courseId));
    }
    
    @ParameterizedTest
    @CsvSource({"1, 1",
                "50, 3",
                "12, 5"})
    void removeStudentFromCourseShouldReturnTrueIfStudentIdLessThan50AndCourseIdLessThan6Test(int studentId, int courseId) throws SQLException {
        assertEquals(true, schoolManager.removeStudentFromCourse(studentId, courseId));
    }
    
    @ParameterizedTest
    @CsvSource({"-1, 1",
                "51, 4",
                "100, 5",
                "1, 6",
                "10, 9",
                "40, -5",
                "-1, 60",
                "1000, 1000",
                "-100, 500"})
    void removeStudentFromCourseShouldReturnFalseIfStudentIdMoreThan50OrCourseIdMoreThan5Test(int studentId, int courseId) throws SQLException {
        assertEquals(false, schoolManager.addStudentToCourse(studentId, courseId));
    }
    
    @ParameterizedTest
    @CsvSource({"' ',''",
                "'    ',''",
                "'  a   ','a'",
                "'\tbbb\t','bbb'"})
    void askStringUserInputShouldReturnTrimmedInputFromSystemInTest(String input, String expected) {
        InputStream stdIn = System.in;
        InputStream consoleInput = new ByteArrayInputStream(input.getBytes());
        System.setIn(consoleInput);
        
        SchoolManager schoolManagerForConsoleInput = new SchoolManager(studentRepository, groupRepository, courseRepository);
        
        assertEquals(expected, schoolManagerForConsoleInput.askStringUserInput());
        System.setIn(stdIn);
    }
    
    
    @ParameterizedTest
    @CsvSource({"' '",
                "a",
                "   a   ",
                "abc",
                "aaa111",
                "111aaa",
                "1a2b3c"})
    void askIntUserInputShouldThrowExceptionIfTrimmedInputIsNotANumberTest(String input) {
        InputStream stdIn = System.in;
        InputStream consoleInput = new ByteArrayInputStream(input.getBytes());
        System.setIn(consoleInput);
        
        SchoolManager schoolManagerForConsoleInput = new SchoolManager(studentRepository, groupRepository, courseRepository);
        
        Throwable thrown = assertThrows(NumberFormatException.class, () -> {
            schoolManagerForConsoleInput.askIntUserInput();
        });
        
        String expected = "For input string: \"" + input.trim() + "\"";
        assertEquals(expected, thrown.getMessage());
        
        System.setIn(stdIn);
    }
    
    @ParameterizedTest
    @CsvSource({"'1', 1",
                "'123', 123",
                "'  123', 123",
                "'123   ', 123",
                "\t 111\t , 111"})
    void askIntUserInputShouldReturnIntegerValueOfTrimmedInputTest(String input, int expected) {
        InputStream stdIn = System.in;
        InputStream consoleInput = new ByteArrayInputStream(input.getBytes());
        System.setIn(consoleInput);
        
        SchoolManager schoolManagerForConsoleInput = new SchoolManager(studentRepository, groupRepository, courseRepository);
        
        assertEquals(expected, schoolManagerForConsoleInput.askIntUserInput());
        System.setIn(stdIn);
    }
}
