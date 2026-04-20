package com.andrei.demo.repository.seeding;

import com.andrei.demo.config.exceptions.ValidationException;
import com.andrei.demo.model.dto.*;
import com.andrei.demo.service.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class DataSeeder {

    private final PersonService personService;
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @EventListener
    public void seedData(ContextRefreshedEvent event) throws IOException, ValidationException {
        List<PersonCreateDTO> people = loadFromJson("people.json", new TypeReference<List<PersonCreateDTO>>() {});
        List<StudentCreateDTO> students = loadFromJson("students.json", new TypeReference<List<StudentCreateDTO>>() {});
        List<ProfessorCreateDTO> professors = loadFromJson("professors.json", new TypeReference<List<ProfessorCreateDTO>>() {});
        List<CourseCreateDTO> courses = loadFromJson("courses.json", new TypeReference<List<CourseCreateDTO>>() {});
        List<EnrollmentBulkDTO> enrollments = loadFromJson("enrollments.json", new TypeReference<List<EnrollmentBulkDTO>>() {});

        personService.addPeople(people);
        studentService.addStudents(students);
        professorService.addProfessors(professors);
        courseService.addCourses(courses);
        enrollmentService.addEnrollments(enrollments);
    }

    private <T> List<T> loadFromJson(String filename, TypeReference<List<T>> typeReference) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource(filename);
        return mapper.readValue(resource.getInputStream(), typeReference);
    }
}
