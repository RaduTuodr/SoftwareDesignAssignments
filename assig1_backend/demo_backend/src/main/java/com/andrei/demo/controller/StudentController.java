package com.andrei.demo.controller;

import com.andrei.demo.config.DuplicateEmailException;
import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Student;
import com.andrei.demo.model.StudentCreateDTO;
import com.andrei.demo.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    @GetMapping()
    public List<Student> getStudents() {
        return studentService.getStudents();
    }

    @GetMapping("/{uuid}")
    public Student getStudentById(@PathVariable UUID uuid) throws ValidationException {
        return studentService.getStudentById(uuid);
    }

    @GetMapping("/email/{email}")
    public Student getStudentByEmail(@PathVariable String email) throws ValidationException {
        return studentService.getStudentByEmail(email);
    }

    @PostMapping
    public Student addStudent(@Valid @RequestBody StudentCreateDTO studentDTO) throws DuplicateEmailException {
        return studentService.addStudent(studentDTO);
    }

    @PutMapping("/{uuid}")
    public Student updateStudent(@PathVariable UUID uuid, @RequestBody Student student)
            throws ValidationException {
        return studentService.updateStudent(uuid, student);
    }

    @DeleteMapping("/{uuid}")
    public void deleteStudent(@PathVariable UUID uuid) {
        studentService.deleteStudent(uuid);
    }
}
