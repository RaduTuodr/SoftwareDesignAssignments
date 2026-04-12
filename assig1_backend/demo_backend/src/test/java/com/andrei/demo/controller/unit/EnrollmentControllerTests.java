package com.andrei.demo.controller.unit;

import com.andrei.demo.controller.EnrollmentController;
import com.andrei.demo.model.Enrollment;
import com.andrei.demo.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentControllerTests {
    @Mock
    private EnrollmentService service;
    @InjectMocks
    private EnrollmentController controller;

    @Test
    void allMethodsDelegate() {
        UUID personId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Enrollment enrollment = new Enrollment();

        when(service.getEnrollments()).thenReturn(List.of(enrollment));
        when(service.getEnrollmentByPersonId(personId)).thenReturn(List.of(enrollment));
        when(service.getEnrollmentByCourseId(courseId)).thenReturn(List.of(enrollment));
        when(service.addEnrollment(personId, courseId)).thenReturn(enrollment);

        assertEquals(1, controller.getEnrollments().size());
        assertEquals(1, controller.getEnrollmentsByPersonId(personId).size());
        assertEquals(1, controller.getEnrollmentsByCourseId(courseId).size());
        assertEquals(enrollment, controller.enrollPersonToCourse(personId, courseId));
    }
}
