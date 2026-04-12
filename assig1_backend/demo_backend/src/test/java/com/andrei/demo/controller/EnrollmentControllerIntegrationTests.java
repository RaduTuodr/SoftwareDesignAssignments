package com.andrei.demo.controller;

import com.andrei.demo.model.Course;
import com.andrei.demo.model.Person;
import com.andrei.demo.repository.CourseRepository;
import com.andrei.demo.repository.EnrollmentRepository;
import com.andrei.demo.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class EnrollmentControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CourseRepository courseRepository;

    private Person person;
    private Course course;

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();
        enrollmentRepository.flush();
        courseRepository.deleteAll();
        personRepository.deleteAll();

        person = new Person();
        person.setName("Enroll User");
        person.setPassword("Strong123!");
        person.setAge(20);
        person.setEmail("enroll.user@example.com");
        person = personRepository.save(person);

        course = new Course();
        course.setTitle("Distributed Systems");
        course.setDescription("Course");
        course.setCredits(6);
        course = courseRepository.save(course);
    }

    @Test
    void enrollPersonToCourseAndQueryByFilters() throws Exception {
        mockMvc.perform(post("/enroll/person/{personId}/course/{courseId}", person.getId(), course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.person.id").value(person.getId().toString()))
                .andExpect(jsonPath("$.course.id").value(course.getId().toString()))
                .andExpect(jsonPath("$.enrollmentDate").exists());

        mockMvc.perform(get("/enroll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/enroll/person/{personId}", person.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].person.email").value("enroll.user@example.com"));

        mockMvc.perform(get("/enroll/course/{courseId}", course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].course.title").value("Distributed Systems"));
    }
}
