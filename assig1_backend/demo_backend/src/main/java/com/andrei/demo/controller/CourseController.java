package com.andrei.demo.controller;

import com.andrei.demo.config.exceptions.ValidationException;
import com.andrei.demo.controller.annotations.IsAdmin;
import com.andrei.demo.controller.annotations.IsStudent;
import com.andrei.demo.model.Course;
import com.andrei.demo.model.dto.CourseCreateDTO;
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

    @IsStudent
    @GetMapping()
    public List<Course> getCourses() { return service.getCourses(); }

    @IsStudent
    @GetMapping("/{uuid}")
    public Course getCourseById(@PathVariable UUID uuid) { return service.getCourseById(uuid); }

    @IsStudent
    @GetMapping("/title/{title}")
    public Course getCourseByTitle(@PathVariable String title) { return service.getCourseByTitle(title); }

    @IsStudent
    @GetMapping("/credits/{credits}")
    public List<Course> getCourseByCredits(@PathVariable Integer credits) { return service.getCoursesByCredits(credits); }

    @IsAdmin
    @PostMapping()
    public Course createCourse(@Valid @RequestBody CourseCreateDTO courseCreateDTO) {
        return service.addCourse(courseCreateDTO);
    }

    @IsAdmin
    @PutMapping("/{uuid}")
    public Course updateCourse(@RequestBody Course course, @PathVariable UUID uuid) throws ValidationException {
        return service.updateCourse(uuid, course);
    }

    @IsAdmin
    @DeleteMapping("/{uuid}")
    public void deleteCourse(@Valid @PathVariable UUID uuid) {
        service.deleteCourse(uuid);
    }
}
