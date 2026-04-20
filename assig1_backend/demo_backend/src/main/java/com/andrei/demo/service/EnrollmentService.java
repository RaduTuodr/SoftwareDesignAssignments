package com.andrei.demo.service;

import com.andrei.demo.model.Course;
import com.andrei.demo.model.Enrollment;
import com.andrei.demo.model.Student;
import com.andrei.demo.model.dto.EnrollmentBulkDTO;
import com.andrei.demo.repository.EnrollmentRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository repository;
    private final StudentService studentService;
    private final CourseService courseService;

    public List<Enrollment> getEnrollments() { return repository.findAll(); }

    public Enrollment getEnrollmentById(UUID uuid) { return repository.getEnrollmentById(uuid); }

    public List<Enrollment> getEnrollmentByCourseId(UUID courseId) {
        Course course = courseService.getCourseById(courseId);
        return repository.getEnrollmentsByCourse(course);
    }

    public List<Enrollment> getEnrollmentByStudentId(UUID studentId) {
        Student student = studentService.getStudentById(studentId);
        return repository.getEnrollmentsByStudent(student);
    }

    public Enrollment addEnrollment(UUID studentId, UUID courseId) {
        Student student = studentService.getStudentById(studentId);
        Course course = courseService.getCourseById(courseId);
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        return repository.save(enrollment);
    }

    public Enrollment updateEnrollment(Enrollment enrollment) { return repository.save(enrollment); }

    public void deleteEnrollment(UUID uuid) { repository.deleteById(uuid); }

    public void addEnrollments(List<EnrollmentBulkDTO> dtos) {
        for (EnrollmentBulkDTO dto : dtos) {
            Student student = studentService.getStudentByEmail(dto.getStudentEmail());
            Course course = courseService.getCourseByTitle(dto.getCourseTitle());
            addEnrollment(student.getId(), course.getId());
        }
    }
}
