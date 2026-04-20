package com.andrei.demo.controller;

import com.andrei.demo.controller.annotations.IsAdmin;
import com.andrei.demo.controller.annotations.IsStudent;
import com.andrei.demo.model.Enrollment;
import com.andrei.demo.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/enroll")
public class EnrollmentController {

    private final EnrollmentService service;

    @IsStudent
    @GetMapping
    public List<Enrollment> getEnrollments() { return service.getEnrollments(); }
    
    @IsStudent
    @GetMapping("/student/{studentId}")
    public List<Enrollment> getEnrollmentsByStudentId(@Valid @PathVariable UUID studentId) { return service.getEnrollmentByStudentId(studentId); }

    @IsStudent
    @GetMapping("/course/{courseId}")
    public List<Enrollment> getEnrollmentsByCourseId(@Valid @PathVariable UUID courseId) { return service.getEnrollmentByCourseId(courseId); }
    
    @IsAdmin
    @PostMapping("/student/{studentId}/course/{courseId}")
    public Enrollment enrollStudentToCourse(@Valid @PathVariable UUID studentId, @Valid @PathVariable UUID courseId) {
        return service.addEnrollment(studentId, courseId);
    }
}
