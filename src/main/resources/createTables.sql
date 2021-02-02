DROP TABLE IF EXISTS students_courses;

DROP TABLE IF EXISTS courses, groups, students;

CREATE TABLE groups(
group_id SERIAL PRIMARY KEY,
group_name text NOT NULL);

CREATE TABLE students(
student_id SERIAL PRIMARY KEY,
group_id integer REFERENCES groups(group_id) ON DELETE RESTRICT,
first_name text NOT NULL,
last_name text NOT NULL);

CREATE TABLE courses(
course_id SERIAL PRIMARY KEY,
course_name text NOT NULL,
course_description text);

CREATE TABLE students_courses(
student_id integer REFERENCES students(student_id) ON DELETE CASCADE,
course_id integer REFERENCES courses(course_id) ON DELETE CASCADE,
PRIMARY KEY (student_id, course_id));
