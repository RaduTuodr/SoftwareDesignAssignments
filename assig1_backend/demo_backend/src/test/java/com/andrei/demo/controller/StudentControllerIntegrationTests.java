package com.andrei.demo.controller;

import com.andrei.demo.model.Student;
import com.andrei.demo.repository.StudentRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class StudentControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    private Student existing;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        studentRepository.flush();

        existing = new Student();
        existing.setName("Student One");
        existing.setPassword("Strong123!");
        existing.setAge(21);
        existing.setEmail("student.one@example.com");
        existing.setRegistrationNumber("REG-100");
        existing.setGraduationYear(2027);
        existing = studentRepository.save(existing);
    }

    @Test
    void getStudentsReturnsSeededData() throws Exception {
        mockMvc.perform(get("/student").with(user("student.one@example.com").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("student.one@example.com"));
    }

    @Test
    void addStudentValidPayload() throws Exception {
        String payload = """
                {
                  "name": "Student Two",
                  "password": "Strong456!",
                  "age": 22,
                  "email": "student.two@example.com",
                  "registrationNumber": "REG-101",
                  "graduationYear": 2028
                }
                """;

        mockMvc.perform(post("/student")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.registrationNumber").value("REG-101"));
    }

    @Test
    void addStudentInvalidPayload() throws Exception {
        String payload = """
                {
                  "name": "",
                  "password": "weak",
                  "age": null,
                  "email": ""
                }
                """;

        mockMvc.perform(post("/student")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name should be between 2 and 100 characters"))
                .andExpect(jsonPath("$.password").value(Matchers.containsString("Password must contain")))
                .andExpect(jsonPath("$.age").value("Age is required"))
                .andExpect(jsonPath("$.email").value("Email is required"));
    }

    @Test
    void updateStudentAndFetchById() throws Exception {
        UUID id = existing.getId();
        String payload = """
                {
                  "name": "Updated Name",
                  "password": "NewStrong1!",
                  "age": 23,
                  "email": "student.updated@example.com",
                  "registrationNumber": "REG-200",
                  "graduationYear": 2029
                }
                """;

        mockMvc.perform(put("/student/{id}", id)
                        .with(user("admin@example.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));

        mockMvc.perform(get("/student/{id}", id).with(user("prof@example.com").roles("PROFESSOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("student.updated@example.com"));
    }

    @Test
    void addStudentRejectsStudentRole() throws Exception {
        String payload = """
                {
                  "name": "Student Two",
                  "password": "Strong456!",
                  "age": 22,
                  "email": "student.two@example.com",
                  "registrationNumber": "REG-101",
                  "graduationYear": 2028
                }
                """;

        mockMvc.perform(post("/student")
                        .with(user("student.one@example.com").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    void getStudentRejectsVisitorRole() throws Exception {
        mockMvc.perform(get("/student/{id}", existing.getId())
                        .with(user("visitor@example.com").roles("VISITOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getStudentsRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/student"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addStudentRejectsDuplicateEmail() throws Exception {
        String payload = """
                {
                  "name": "Student Two",
                  "password": "Strong456!",
                  "age": 22,
                  "email": "student.one@example.com",
                  "registrationNumber": "REG-101",
                  "graduationYear": 2028
                }
                """;

        mockMvc.perform(post("/student")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.email").value("Email student.one@example.com already exists"));
    }

    @Test
    void getStudentRejectsMalformedUuid() throws Exception {
        mockMvc.perform(get("/student/{id}", "not-a-uuid")
                        .with(user("student.one@example.com").roles("STUDENT")))
                .andExpect(status().isBadRequest());
    }
}
