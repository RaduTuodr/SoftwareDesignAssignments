package com.andrei.demo.repository;

import com.andrei.demo.model.Course;
import com.andrei.demo.model.Enrollment;
import com.andrei.demo.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    Enrollment getEnrollmentById(UUID id);

    Enrollment getEnrollmentByCourse(Course course);

    Enrollment getEnrollmentByPerson(Person person);
}
