package com.foxminded.school.controller.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import com.foxminded.school.model.Course;
import com.foxminded.school.model.Student;
import com.foxminded.school.controller.dao.CourseDao;
import com.foxminded.school.controller.dao.StudentDao;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentRepositoryTest {
    private static final int CREATE_TEST_NUMBER = 1;
    private static final int DELETE_TEST_NUMBER = 2;
    private static final int ADD_STUDENTS_COURSE_TEST_NUMBER = 3;
    private static final int DELETE_STUDENTS_COURSE_TEST_NUMBER = 4;
    
    CourseDao courseDao;
    StudentDao studentDao;
    
    StudentRepository studentRepository;
    
    List<Course> courses;
    
    @BeforeAll
    void init() throws SQLException {
        courseDao = Mockito.mock(CourseDao.class);
        studentDao = Mockito.mock(StudentDao.class);
        
        studentRepository = new StudentRepository(studentDao, courseDao);
        
        courses = new ArrayList<>();
        courses.add(new Course(1, "math"));
        courses.add(new Course(2, "programming"));
        courses.add(new Course(3, "biology"));
        
        Student mockStudent = new Student("Name", "LastName");
        mockStudent.setId(1);
        
        Mockito.when(courseDao.getByStudent(mockStudent)).thenReturn(courses);
        Mockito.when(studentDao.getPartialById(1)).thenReturn(mockStudent);
        Mockito.when(studentDao.create(Mockito.any(Student.class))).thenReturn(new int[] {CREATE_TEST_NUMBER});
        Mockito.when(studentDao.delete(Mockito.any(Student.class))).thenReturn(DELETE_TEST_NUMBER);
        Mockito.when(studentDao.addStudentsCourse(Mockito.any(Student.class), Mockito.any(Course.class)))
                .thenReturn(ADD_STUDENTS_COURSE_TEST_NUMBER);
        Mockito.when(studentDao.deleteStudentsCourse(Mockito.any(Student.class), Mockito.any(Course.class)))
                .thenReturn(DELETE_STUDENTS_COURSE_TEST_NUMBER);
    }

    @Test
    void getByIdShouldReturnStudentObjectContainingListOfCoursesTest() throws SQLException {
        Student student = studentRepository.getById(1);
        assertFalse(student == null);
        assertEquals("Name", student.getFirstName());
        assertEquals("LastName", student.getLastName());
        assertFalse(student.getCourses().isEmpty());
        assertSame(courses, student.getCourses());
    }  
    
    @Test
    void addShouldCallDaoWithNewStudentObjectTest() throws SQLException {
        int[] actual = studentRepository.add("aaa", "bbb");
        assertEquals(CREATE_TEST_NUMBER, actual[0]);
    }
    
    @Test
    void deleteShouldCallDeleteInStudentDaoTest() throws SQLException {
        int actual = studentRepository.delete(new Student("Name", "LastName"));
        assertEquals(DELETE_TEST_NUMBER, actual);
    }
    
    @Test
    void addStudentsCourseShouldCalladdStudentsCourseInStudentDaoTest() throws SQLException {
        Student testStudent = new Student("name", "lasName");
        Course testCourse = new Course(1, "course");
        int actual = studentRepository.addStudentsCourse(testStudent, testCourse);
        assertEquals(ADD_STUDENTS_COURSE_TEST_NUMBER, actual);
    }
    
    @Test
    void deleteStudentsCourseShouldCalldeleteStudentsCourseInStudentDaoTest() throws SQLException {
        Student testStudent = new Student("name", "lasName");
        Course testCourse = new Course(1, "course");
        int actual = studentRepository.deleteStudentsCourse(testStudent, testCourse);
        assertEquals(DELETE_STUDENTS_COURSE_TEST_NUMBER, actual);
    }
}
