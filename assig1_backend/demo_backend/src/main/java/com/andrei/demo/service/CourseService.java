package com.andrei.demo.service;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Course;
import com.andrei.demo.model.CourseCreateDTO;
import com.andrei.demo.repository.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CourseService {

    private final CourseRepository repository;

    public List<Course> getCourses() { return repository.findAll(); }

    public Course getCourseById(UUID uuid) {
        return repository.findById(uuid).orElseThrow(
                () -> new IllegalStateException("Course with id " + uuid + " not found"));
    }

    public Course getCourseByTitle(String title) {
        return repository.findByTitle(title).orElseThrow(
                () -> new IllegalStateException("Course with title " + title + " not found"));
    }

    public List<Course> getCoursesByCredits(Integer credits) {
        return repository.findByCredits(credits).orElseThrow(
                () -> new IllegalStateException("Courses with credits " + credits + " not found"));
    }

    public Course createCourse(CourseCreateDTO courseCreateDTO) {
        Course course = new Course();

        course.setTitle(courseCreateDTO.getTitle());
        course.setDescription(courseCreateDTO.getDescription());
        course.setCredits(courseCreateDTO.getCredits());

        return repository.save(course);
    }

    public Course updateCourse(UUID uuid, Course course) throws ValidationException {
        Optional<Course> courseOptional = repository.findById(uuid);

        if (courseOptional.isEmpty()) {
            throw new ValidationException("Person with id " + uuid + " not found");
        }
        Course existingCourse = courseOptional.get();

        existingCourse.setTitle(course.getTitle());
        existingCourse.setDescription(course.getDescription());
        existingCourse.setCredits(course.getCredits());

        return repository.save(existingCourse);
    }

    public void deleteCourse(UUID uuid) {
        repository.deleteById(uuid);
    }
}
