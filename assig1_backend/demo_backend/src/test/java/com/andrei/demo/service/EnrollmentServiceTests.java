package com.andrei.demo.service;

import com.andrei.demo.model.Course;
import com.andrei.demo.model.Enrollment;
import com.andrei.demo.model.Person;
import com.andrei.demo.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTests {
    @Mock
    private EnrollmentRepository repository;
    @Mock
    private PersonService personService;
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
        Enrollment e = new Enrollment();
        when(repository.getEnrollmentById(id)).thenReturn(e);
        assertEquals(e, service.getEnrollmentById(id));
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
    void getEnrollmentByPersonIdUsesPersonLookup() {
        UUID personId = UUID.randomUUID();
        Person person = new Person();
        when(personService.getPersonById(personId)).thenReturn(person);
        when(repository.getEnrollmentsByPerson(person)).thenReturn(List.of(new Enrollment()));

        assertEquals(1, service.getEnrollmentByPersonId(personId).size());
    }

    @Test
    void addEnrollmentBuildsAndSaves() {
        UUID personId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Person person = new Person();
        Course course = new Course();

        when(personService.getPersonById(personId)).thenReturn(person);
        when(courseService.getCourseById(courseId)).thenReturn(course);
        when(repository.save(any(Enrollment.class))).thenAnswer(inv -> inv.getArgument(0));

        Enrollment result = service.addEnrollment(personId, courseId);

        assertEquals(person, result.getPerson());
        assertEquals(course, result.getCourse());
        assertNotNull(result.getEnrollmentDate());
    }

    @Test
    void updateEnrollmentDelegates() {
        Enrollment e = new Enrollment();
        when(repository.save(e)).thenReturn(e);
        assertEquals(e, service.updateEnrollment(e));
    }

    @Test
    void deleteEnrollmentDelegates() {
        UUID id = UUID.randomUUID();
        service.deleteEnrollment(id);
        verify(repository).deleteById(id);
    }
}
