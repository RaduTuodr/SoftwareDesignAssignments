package com.andrei.demo.controller;

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

    @GetMapping
    public List<Enrollment> getEnrollments() { return service.getEnrollments(); }
    
    @GetMapping("/person/{personId}")
    public List<Enrollment> getEnrollmentsByPersonId(@Valid @PathVariable UUID personId) { return service.getEnrollmentByPersonId(personId); }
    
    @GetMapping("/course/{courseId}")
    public List<Enrollment> getEnrollmentsByCourseId(@Valid @PathVariable UUID courseId) { return service.getEnrollmentByCourseId(courseId); }
    
    @PostMapping("/person/{personId}/course/{courseId}")
    public Enrollment enrollPersonToCourse(@Valid @PathVariable UUID personId, @Valid @PathVariable UUID courseId) {
        return service.addEnrollment(personId, courseId);
    }
}
