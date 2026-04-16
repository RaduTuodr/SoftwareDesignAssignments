package com.andrei.demo.service;

import com.andrei.demo.model.LoginResponse;
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

    public LoginResponse login(String email, String password) {
        Optional<Person> maybePerson = personRepository.findByEmail(email);
        if(maybePerson.isEmpty()) {
            return new LoginResponse(
                    false,
                    null,
                    "Person with email " + email + " not found"
            );
        }
        Person person = maybePerson.get();
        if (passwordEncoder.matches(password, person.getPassword())) {
            String role;
            if (person instanceof Student) {
                role = "STUDENT";
            } else if (person instanceof Professor) {
                role = "PROFESSOR";
            } else {
                role = "VIEWER";
            }
            return new LoginResponse(true, role, null);
        } else {
            return new LoginResponse(false, null, "Incorrect password");
        }
    }
}