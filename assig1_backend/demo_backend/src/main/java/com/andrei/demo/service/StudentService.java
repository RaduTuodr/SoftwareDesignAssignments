package com.andrei.demo.service;

import com.andrei.demo.config.DuplicateEmailException;
import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Student;
import com.andrei.demo.model.StudentCreateDTO;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final PersonRepository personRepository;

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(UUID uuid) throws ValidationException {
        return studentRepository.findById(uuid).orElseThrow(
                () -> new ValidationException("Student with id " + uuid + " not found"));
    }

    public Student getStudentByEmail(String email) throws ValidationException {
        return studentRepository.findByEmail(email).orElseThrow(
                () -> new ValidationException("Student with email " + email + " not found"));
    }

    public Student addStudent(StudentCreateDTO studentDTO) throws DuplicateEmailException {

        if (personRepository.existsByEmail(studentDTO.getEmail())) {
            throw new DuplicateEmailException("Email " + studentDTO.getEmail() + " already exists");
        }

        Student student = new Student();
        student.setName(studentDTO.getName());
        student.setPassword(studentDTO.getPassword());
        student.setAge(studentDTO.getAge());
        student.setEmail(studentDTO.getEmail());
        student.setRegistrationNumber(studentDTO.getRegistrationNumber());
        student.setGraduationYear(studentDTO.getGraduationYear());
        return studentRepository.save(student);
    }

    public Student updateStudent(UUID uuid, Student student) throws ValidationException, DuplicateEmailException {
        Student existingStudent = studentRepository.findById(uuid).orElseThrow(
                () -> new ValidationException("Student with id " + uuid + " not found"));

        String newEmail = student.getEmail();
        if (newEmail != null && !newEmail.equals(existingStudent.getEmail())
                && personRepository.existsByEmailAndIdNot(newEmail, existingStudent.getId())) {
            throw new DuplicateEmailException("Email " + newEmail + " already exists");
        }

        existingStudent.setName(student.getName());
        existingStudent.setPassword(student.getPassword());
        existingStudent.setAge(student.getAge());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setRegistrationNumber(student.getRegistrationNumber());
        existingStudent.setGraduationYear(student.getGraduationYear());
        return studentRepository.save(existingStudent);
    }

    public void deleteStudent(UUID uuid) {
        studentRepository.deleteById(uuid);
    }
}
