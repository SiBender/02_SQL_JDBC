package com.foxminded.school.controller.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.foxminded.school.controller.dao.*;
import com.foxminded.school.model.*;

public class DBInitializer {
    private static final GroupDao groupDao = new GroupDao();
    private static final CourseDao courseDao = new CourseDao();
    private static final StudentDao studentDao = new StudentDao();
    private static final SQLScriptRunner scriptRunner = new SQLScriptRunner();
    
    private Random random = new Random();
    
    private static final int NUMBER_OF_STUDENTS = 200;
    private static final String[] FIRST_NAMES = { "Ali", "Elon", "Alan", "Lana", "Stewen", "Ria", "Kira", "Stephen", "Clay", 
            "Lacy", "Leni", "Neil", "Hans", "Nash", "Iris", "Siri", "Alice", "Lecia", "Rick", "Morty" };
    private static final String[] LAST_NAMES = { "Smith", "Anders", "Simpson", "Wick", "Griffin", "Hawking", "Stark", "Lanister", "Musk", 
            "McCormick", "Heisenberg", "Moore", "Freeman", "Crick", "Watson", "Hetfield", "Cobain", "Lecter", "Skywalker", "Vader" };
    
    private static final String[] COURSES = {"programming", "math", "russian language", "biology", "geography",
                                            "chemistry", "physics", "music", "mixed martial arts", "literature"};
   
    private static final int NUMBER_OF_GROUPS = 10;
    private static final int NUMBER_OF_ENGLISH_LETTERS = 26;
    private static final int MAX_COURSES_PER_STUDENT = 3;
    
    public void init() throws IOException, SQLException {
        creataTables();
        fillCoursesTable();
        fillGroupsTable();
        fillStudentsTable();
    }

    private void creataTables() throws IOException, SQLException {
        scriptRunner.init();
    }

    private void fillGroupsTable() throws SQLException {
        Set<String> groupNames = generateGroupNames(NUMBER_OF_GROUPS);
        List<Group> groups = new ArrayList<>();
        for (String groupName : groupNames) {
            groups.add(new Group(groupName));
        }
        groupDao.create(groups);
    }
    
    private Set<String> generateGroupNames(int count) {
        Set<String> groups = new HashSet<>();

        while (groups.size() < count) {
            StringBuilder groupName = new StringBuilder();

            groupName.append((char) ('a' + random.nextInt(NUMBER_OF_ENGLISH_LETTERS)));
            groupName.append((char) ('a' + random.nextInt(NUMBER_OF_ENGLISH_LETTERS)));
            groupName.append(String.format("-%02d", random.nextInt(100)));

            groups.add(groupName.toString());
        }
        return groups;
    }

    private void fillCoursesTable() throws SQLException {
        List<Course> courses = new ArrayList<>();
        for (String courseName : COURSES) {
            Course course = new Course(0, courseName);
            course.setDescription("information about " + courseName);
            courses.add(course);
        }
        courseDao.create(courses);
    }
    
    private void fillStudentsTable() throws SQLException {
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_STUDENTS; i++) {
            Student student = new Student(FIRST_NAMES[random.nextInt(FIRST_NAMES.length)], LAST_NAMES[random.nextInt(LAST_NAMES.length)]);
            student.setId(i + 1);
            int groupId = random.nextInt(NUMBER_OF_GROUPS + 1);
            if (groupId > 0) {
                student.setGroup(groupId);
            }
            students.add(student);
        }
        studentDao.create(students);
        
        fillStudentsCoursesRelation(students);
    }
    
    private void fillStudentsCoursesRelation(List<Student> students) throws SQLException {
        List<Integer> coursesIds = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        for (Student student : students) {
            int numOfCourses = random.nextInt(MAX_COURSES_PER_STUDENT) + 1;
            Collections.shuffle(coursesIds);
            List<Course> courses = new ArrayList<>();
            for (int j = 0; j < numOfCourses; j++) {
                Course course = new Course(coursesIds.get(j), COURSES[coursesIds.get(j) - 1]);
                courses.add(course);
            }
            student.setCourses(courses);
        }
        studentDao.assignCourses(students);
    }
}
