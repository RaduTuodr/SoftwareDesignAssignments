package com.andrei.demo.service;

import com.andrei.demo.model.Course;
import com.andrei.demo.model.Enrollment;
import com.andrei.demo.model.Person;
import com.andrei.demo.repository.EnrollmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository repository;

    public List<Enrollment> getEnrollments() { return repository.findAll(); }

    public Enrollment getEnrollmentById(UUID uuid) { return repository.getEnrollmentById(uuid); }

    public Enrollment getEnrollmentByCourse(Course course) { return repository.getEnrollmentByCourse(course); }

    public Enrollment getEnrollmentByPerson(Person person) { return repository.getEnrollmentByPerson(person); }
}
