package com.andrei.demo.controller.unit;

import com.andrei.demo.controller.StudentController;
import com.andrei.demo.model.Student;
import com.andrei.demo.model.dto.StudentCreateDTO;
import com.andrei.demo.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentControllerTests {
    @Mock
    private StudentService service;
    @InjectMocks
    private StudentController controller;

    @Test
    void allMethodsDelegate() throws Exception {
        UUID id = UUID.randomUUID();
        Student student = new Student();
        StudentCreateDTO dto = new StudentCreateDTO();

        when(service.getStudents()).thenReturn(List.of(student));
        when(service.getStudentById(id)).thenReturn(student);
        when(service.getStudentByEmail("radu@tudor.com")).thenReturn(student);
        when(service.addStudent(dto)).thenReturn(student);
        when(service.updateStudent(id, student)).thenReturn(student);

        assertEquals(1, controller.getStudents().size());
        assertEquals(student, controller.getStudentById(id));
        assertEquals(student, controller.getStudentByEmail("radu@tudor.com"));
        assertEquals(student, controller.addStudent(dto));
        assertEquals(student, controller.updateStudent(id, student));
        controller.deleteStudent(id);

        verify(service).deleteStudent(id);
    }
}
