package com.andrei.demo.service;

import com.andrei.demo.config.exceptions.DuplicateEmailException;
import com.andrei.demo.config.exceptions.ValidationException;
import com.andrei.demo.model.Student;
import com.andrei.demo.model.StudentCreateDTO;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTests {
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private PersonRepository personRepository;
    @InjectMocks
    private StudentService studentService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(UUID.randomUUID());
        student.setEmail("std@example.com");
        student.setName("Stud");
    }

    @Test
    void getStudentsReturnsAll() {
        when(studentRepository.findAll()).thenReturn(List.of(student));
        assertEquals(1, studentService.getStudents().size());
    }

    @Test
    void getStudentByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(studentRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> studentService.getStudentById(id));
    }

    @Test
    void getStudentByEmailThrowsWhenMissing() {
        when(studentRepository.findByEmail("x@x.com")).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> studentService.getStudentByEmail("x@x.com"));
    }

    @Test
    void addStudentThrowsWhenDuplicateEmail() {
        StudentCreateDTO dto = new StudentCreateDTO();
        dto.setEmail("dup@example.com");
        when(personRepository.existsByEmail("dup@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> studentService.addStudent(dto));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void addStudentSavesEntity() {
        StudentCreateDTO dto = new StudentCreateDTO();
        dto.setEmail("new@example.com");
        dto.setName("N");
        dto.setPassword("Aaa123!!");
        dto.setAge(20);
        dto.setRegistrationNumber("R1");
        dto.setGraduationYear(2028);

        when(personRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        try {
            Student result = studentService.addStudent(dto);

            assertEquals("R1", result.getRegistrationNumber());
            assertEquals(2028, result.getGraduationYear());
        } catch (ValidationException e) {
            fail("ValidationException should not be thrown");
        }
    }

    @Test
    void updateStudentThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(studentRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> studentService.updateStudent(id, new Student()));
    }

    @Test
    void updateStudentThrowsWhenEmailUsed() {
        UUID id = student.getId();
        Student patch = new Student();
        patch.setEmail("taken@example.com");

        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(personRepository.existsByEmailAndIdNot("taken@example.com", id)).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> studentService.updateStudent(id, patch));
    }

    @Test
    void updateStudentUpdatesAndSaves() throws ValidationException, DuplicateEmailException {
        UUID id = student.getId();
        Student patch = new Student();
        patch.setName("Upd");
        patch.setEmail("upd@example.com");

        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(personRepository.existsByEmailAndIdNot("upd@example.com", id)).thenReturn(false);
        when(studentRepository.save(student)).thenReturn(student);

        Student result = studentService.updateStudent(id, patch);

        assertEquals("Upd", result.getName());
    }

    @Test
    void deleteStudentDelegates() {
        UUID id = UUID.randomUUID();
        studentService.deleteStudent(id);
        verify(studentRepository).deleteById(id);
    }
}
