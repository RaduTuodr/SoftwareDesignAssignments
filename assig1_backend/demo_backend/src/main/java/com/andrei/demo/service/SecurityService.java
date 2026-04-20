package com.andrei.demo.service;

import com.andrei.demo.model.dto.LoginResponseDTO;
import com.andrei.demo.model.Person;
import com.andrei.demo.model.Professor;
import com.andrei.demo.model.Student;
import com.andrei.demo.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class SecurityService {
    private final PersonRepository personRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public LoginResponseDTO login(String email, String password) {
        Optional<Person> maybePerson = personRepository.findByEmail(email);
        if(maybePerson.isEmpty()) {
            return new LoginResponseDTO(
                    false,
                    null,
                    null,
                    "Person with email " + email + " not found"
            );
        }
        Person person = maybePerson.get();
        if (passwordEncoder.matches(password, person.getPassword())) {
            String role;
            if (person.getEnrollments()) {
            } else if (person instanceof Student) {
                role = "STUDENT";
            } else if (person instanceof Professor) {
                role = "PROFESSOR";
            } else {
                role = "VISITOR";
            }
            return new LoginResponseDTO(true, role, jwtService.generateToken(email, role), null);
        } else {
            return new LoginResponseDTO(false, null, null, "Incorrect password");
        }
    }
}