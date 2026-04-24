package com.andrei.demo.service;

import com.andrei.demo.model.Course;
import com.andrei.demo.model.Enrollment;
import com.andrei.demo.model.Student;
import com.andrei.demo.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTests {
    @Mock
    private EnrollmentRepository repository;
    @Mock
    private StudentService studentService;
    @Mock
    private CourseService courseService;
    @InjectMocks
    private EnrollmentService service;

    @Test
    void getEnrollmentsReturnsAll() {
        when(repository.findAll()).thenReturn(List.of(new Enrollment()));
        assertEquals(1, service.getEnrollments().size());
    }

    @Test
    void getEnrollmentByIdDelegates() {
        UUID id = UUID.randomUUID();
        Enrollment enrollment = new Enrollment();
        when(repository.getEnrollmentById(id)).thenReturn(enrollment);
        assertEquals(enrollment, service.getEnrollmentById(id));
    }

    @Test
    void getEnrollmentByCourseIdUsesCourseLookup() {
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        when(courseService.getCourseById(courseId)).thenReturn(course);
        when(repository.getEnrollmentsByCourse(course)).thenReturn(List.of(new Enrollment()));

        assertEquals(1, service.getEnrollmentByCourseId(courseId).size());
    }

    @Test
    void getEnrollmentByStudentIdUsesStudentLookup() {
        UUID studentId = UUID.randomUUID();
        Student student = new Student();
        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(repository.getEnrollmentsByStudent(student)).thenReturn(List.of(new Enrollment()));

        assertEquals(1, service.getEnrollmentByStudentId(studentId).size());
    }

    @Test
    void addEnrollmentBuildsAndSaves() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Student student = new Student();
        Course course = new Course();

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(courseService.getCourseById(courseId)).thenReturn(course);
        when(repository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Enrollment result = service.addEnrollment(studentId, courseId);

        assertEquals(student, result.getStudent());
        assertEquals(course, result.getCourse());
        assertNotNull(result.getEnrollmentDate());
    }

    @Test
    void updateEnrollmentDelegates() {
        Enrollment enrollment = new Enrollment();
        when(repository.save(enrollment)).thenReturn(enrollment);
        assertEquals(enrollment, service.updateEnrollment(enrollment));
    }

    @Test
    void deleteEnrollmentDelegates() {
        UUID id = UUID.randomUUID();
        service.deleteEnrollment(id);
        verify(repository).deleteById(id);
    }
}
