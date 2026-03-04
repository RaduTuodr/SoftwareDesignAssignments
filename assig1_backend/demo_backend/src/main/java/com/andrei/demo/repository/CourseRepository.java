package com.andrei.demo.repository;

import com.andrei.demo.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    Optional<Course> findByTitle(String title);

    Optional<List<Course>> findByCredits(Integer credits);
}
