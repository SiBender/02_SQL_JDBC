DROP TABLE IF EXISTS students_courses, courses, groups, students;

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

INSERT INTO groups (group_name) VALUES 
('gr-01'),
('gr-02'),
('gr-03'),
('gr-04'),
('gr-05');

INSERT INTO courses (course_name, course_description) VALUES
('course1', 'course1 description'),
('course2', 'course2 description'),
('course3', 'course3 description'),
('course4', 'course4 description'),
('course5', 'course5 description');

INSERT INTO students (group_id, first_name, last_name) VALUES
(1, 'Ali', 'Smith'),
(1, 'Davy', 'Anders'),
(1, 'Alan', 'Simpson'),
(1, 'Lana', 'Wick'),
(1, 'Ira', 'Griffin'),
(1, 'Ria', 'Jones'),
(1, 'Kira', 'Stark'),
(1, 'Arik', 'Lanister'),
(2, 'Clay', 'Elk'),
(2, 'Lacy', 'McCormick'),
(2, 'Leni', 'Heisenberg'),
(2, 'Neil', 'Moore'),
(2, 'Hans', 'Freeman'),
(3, 'Nash', 'Crick'),
(3, 'Iris', 'Watson'),
(3, 'Siri', 'Hetfield'),
(4, 'Alice', 'Cobain'),
(4, 'Lecia', 'Lecter'),
(5, 'Rick', 'Skywalker'),
(5, 'Morty', 'Vader');

INSERT INTO students_courses (student_id, course_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(2, 4),
(2, 5),
(3, 1),
(3, 2),
(3, 3),
(4, 4),
(4, 5),
(5, 1),
(5, 2),
(5, 3),
(6, 4),
(6, 5),
(6, 1),
(7, 2),
(7, 3),
(8, 4),
(9, 5),
(10, 1),
(11, 2),
(12, 3),
(13, 4),
(14, 5),
(15, 1),
(16, 2),
(17, 3),
(18, 4),
(19, 5),
(19, 1);
