package com.andrei.demo.controller;

import com.andrei.demo.model.Course;
import com.andrei.demo.model.Student;
import com.andrei.demo.repository.CourseRepository;
import com.andrei.demo.repository.EnrollmentRepository;
import com.andrei.demo.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();
        enrollmentRepository.flush();
        courseRepository.deleteAll();
        studentRepository.deleteAll();

        student = new Student();
        student.setName("Enroll Student");
        student.setPassword("Strong123!");
        student.setAge(20);
        student.setEmail("enroll.student@example.com");
        student.setRegistrationNumber("REG-300");
        student.setGraduationYear(2027);
        student = studentRepository.save(student);

        course = new Course();
        course.setTitle("Distributed Systems");
        course.setDescription("Course");
        course.setCredits(6);
        course = courseRepository.save(course);
    }

    @Test
    void enrollStudentToCourseAndQueryByFilters() throws Exception {
        mockMvc.perform(post("/enroll/student/{studentId}/course/{courseId}", student.getId(), course.getId())
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.course.id").value(course.getId().toString()))
                .andExpect(jsonPath("$.enrollmentDate").exists());

        mockMvc.perform(get("/enroll").with(user("student@example.com").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/enroll/student/{studentId}", student.getId())
                        .with(user("prof@example.com").roles("PROFESSOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/enroll/course/{courseId}", course.getId())
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].course.title").value("Distributed Systems"));
    }

    @Test
    void enrollEndpointRejectsStudentRole() throws Exception {
        mockMvc.perform(post("/enroll/student/{studentId}/course/{courseId}", student.getId(), course.getId())
                        .with(user("student@example.com").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void enrollmentQueriesRejectVisitorRole() throws Exception {
        mockMvc.perform(get("/enroll").with(user("visitor@example.com").roles("VISITOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void enrollEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/enroll/student/{studentId}/course/{courseId}", student.getId(), course.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getEnrollmentsByStudentRejectsMalformedUuid() throws Exception {
        mockMvc.perform(get("/enroll/student/{studentId}", "not-a-uuid")
                        .with(user("student@example.com").roles("STUDENT")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void enrollEndpointRejectsMalformedUuid() throws Exception {
        mockMvc.perform(post("/enroll/student/{studentId}/course/{courseId}", "not-a-uuid", course.getId())
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }
}
