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
class CourseRepositoryTest {
    CourseDao courseDao;
    StudentDao studentDao;
    
    CourseRepository courseRepository;
    
    List<Student> students;
    List<Course> courses;
    
    @BeforeAll
    void init() throws SQLException {
        courseDao = Mockito.mock(CourseDao.class);
        studentDao = Mockito.mock(StudentDao.class);
        
        courseRepository = new CourseRepository(courseDao, studentDao);
        
        students = new ArrayList<>();
        students.add(new Student("Alan", "Atkin"));
        students.add(new Student("Benny", "Benasy"));
        students.add(new Student("Chad", "Chaplin"));
        students.add(new Student("Dan", "Drake"));
        
        courses = new ArrayList<>();
        courses.add(new Course(1, "math"));
        courses.add(new Course(2, "programming"));
        courses.add(new Course(3, "biology"));
        courses.add(new Course(4, "music"));
        courses.add(new Course(5, "literature"));
        
        Course mockCourse = new Course(1, "Test course");
        
        Mockito.when(courseDao.getPartialById(1)).thenReturn(mockCourse);
        Mockito.when(courseDao.getAll()).thenReturn(courses);
        Mockito.when(studentDao.getByCourse(mockCourse)).thenReturn(students);
    }
    
    @Test
    void getByIdShouldReturnCourseObjectContainingListOfStudentsTest() throws SQLException {
        Course course = courseRepository.getById(1);
        assertFalse(course == null);
        assertEquals(1, course.getId());
        assertEquals("Test course", course.getName());
        assertFalse(course.getStudents().isEmpty());
        assertSame(students, course.getStudents());
    }

    @Test
    void getAllShouldReturnListOfCourseObjectsTest() throws SQLException {
        List<Course> actualCourses = courseRepository.getAll();
        assertFalse(actualCourses.isEmpty());
        assertSame(courses, actualCourses);
    }
}
