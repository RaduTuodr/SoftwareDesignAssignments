package com.andrei.demo.service;

import com.andrei.demo.config.exceptions.DuplicateEmailException;
import com.andrei.demo.model.dto.*;
import com.andrei.demo.model.Person;
import com.andrei.demo.model.Professor;
import com.andrei.demo.model.Student;
import com.andrei.demo.model.enums.RoleName;
import com.andrei.demo.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginService {
    private final PersonRepository personRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final PersonService personService;

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Optional<Person> maybePerson = personRepository.findByEmail(loginRequestDTO.email());
        if(maybePerson.isEmpty()) {
            return new LoginResponseDTO(
                    false,
                    null,
                    null,
                    null,
                    "Person with email " + loginRequestDTO.email() + " not found"
            );
        }
        Person person = maybePerson.get();
        if (passwordEncoder.matches(loginRequestDTO.password(), person.getPassword())) {
            RoleName role;
            if (person instanceof Student) {
                role = RoleName.STUDENT;
            } else if (person instanceof Professor) {
                Professor prof = (Professor) person;
                if ("Administration".equals(prof.getDepartment())) {
                    role = RoleName.ADMIN;
                } else {
                    role = RoleName.PROFESSOR;
                }
            } else {
                role = RoleName.ADMIN;
            }
            return new LoginResponseDTO(true, role.name(), jwtService.generateToken(loginRequestDTO.email(), role), (int) (System.currentTimeMillis() / 1000 + 60 * 60), null);
        } else {
            return new LoginResponseDTO(false, null, null, null, "Incorrect password");
        }
    }

    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        Optional<Person> maybePerson = personRepository.findByEmail(registerRequestDTO.email());
        if(maybePerson.isPresent()) {
            return new RegisterResponseDTO(
                    false,
                    "Person with email " + registerRequestDTO.email() + " already exists!");
        }

        PersonCreateDTO personCreateDTO = new PersonCreateDTO(
                registerRequestDTO.name(),
                registerRequestDTO.password(),
                registerRequestDTO.age(),
                registerRequestDTO.email()
        );
        try {
            System.out.println("Before saving " + personCreateDTO);
            personService.addPerson(personCreateDTO);
            return new RegisterResponseDTO(
                    true,
                    "Person with email " + registerRequestDTO.email() + " successfully registered!"
            );
        } catch (DuplicateEmailException e) {
            return new RegisterResponseDTO(
                    false,
                    "Person with email " + registerRequestDTO.email() + " already exists!"
            );
        }
    }
}
