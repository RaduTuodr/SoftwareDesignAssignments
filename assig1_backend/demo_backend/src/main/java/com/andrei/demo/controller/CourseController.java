package com.andrei.demo.controller;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Course;
import com.andrei.demo.model.CourseCreateDTO;
import com.andrei.demo.service.CourseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("/course")
public class CourseController {

    private CourseService service;

    @GetMapping()
    public List<Course> getCourses() { return service.getCourses(); }

    @GetMapping("/{uuid}")
    public Course getCourseById(@PathVariable UUID uuid) { return service.getCourseById(uuid); }

    @GetMapping("/title/{title}")
    public Course getCourseByTitle(@PathVariable String title) { return service.getCourseByTitle(title); }

    @GetMapping("/credits/{credits}")
    public List<Course> getCourseByCredits(@PathVariable Integer credits) { return service.getCoursesByCredits(credits); }

    @PostMapping()
    public Course createCourse(@Valid @RequestBody CourseCreateDTO courseCreateDTO) {
        return service.createCourse(courseCreateDTO);
    }

    @PutMapping("/{uuid}")
    public Course updateCourse(@RequestBody Course course, @PathVariable UUID uuid) throws ValidationException {
        return service.updateCourse(uuid, course);
    }

    @DeleteMapping("/{uuid}")
    public void deleteCourse(@Valid @PathVariable UUID uuid) {
        service.deleteCourse(uuid);
    }
}
