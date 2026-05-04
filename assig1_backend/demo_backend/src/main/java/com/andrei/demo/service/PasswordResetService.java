package com.andrei.demo.service;

import com.andrei.demo.config.exceptions.ExpiredCodeException;
import com.andrei.demo.config.exceptions.InvalidCodeException;
import com.andrei.demo.config.exceptions.ValidationException;
import com.andrei.demo.model.Code;
import com.andrei.demo.model.Person;
import com.andrei.demo.model.dto.PasswordChangeConfirmedDTO;
import com.andrei.demo.model.dto.PasswordChangeRequestDTO;
import com.andrei.demo.repository.CodeRepository;
import com.andrei.demo.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PasswordResetService {

    private final PersonRepository personRepository;

    private final PasswordEncoder passwordEncoder;

    private final MessageService messageService;

    private final CodeRepository codeRepository;

    private final SecureRandom random = new SecureRandom();

    public void handlePasswordChangeRequest(UUID uuid, PasswordChangeRequestDTO passwordChangeDTO) throws ValidationException {
        Optional<Person> personOptional =  personRepository.findById(uuid);
        if(personOptional.isEmpty()) {
            throw new ValidationException("Person with id " + uuid + " not found");
        }

        Code code = new Code();
        code.setCode(generateCode());
        code.setExpirationDate(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
        code.setPersonUuid(uuid);
        codeRepository.save(code);

        messageService.sendMessage(passwordChangeDTO.phoneNumber(), code.getCode());
    }

    public void confirmPasswordReset(UUID uuid, PasswordChangeConfirmedDTO passwordChangeConfirmedDTO) throws ValidationException {
        Optional<Person> personOptional =  personRepository.findById(uuid);
        if(personOptional.isEmpty()) {
            throw new ValidationException("Person with id " + uuid + " not found");
        }

        System.out.println("Confirm password reset for person with id " + uuid);

        Person person = personOptional.get();
        Code code = codeRepository.findTopByPersonUuidOrderByExpirationDateDesc((uuid)).orElseThrow(() -> new ValidationException("No code found for person with id " + uuid));

        if (!passwordEncoder.matches(passwordChangeConfirmedDTO.oldPassword(), person.getPassword())) {
            throw new ValidationException("Old password does not match");
        }

        if(code.getExpirationDate().before(new Date())) {
            throw new ExpiredCodeException("Code has expired");
        }

        if(!code.getCode().equals(passwordChangeConfirmedDTO.code())) {
            throw new InvalidCodeException("Invalid code");
        }

         person.setPassword(passwordEncoder.encode(passwordChangeConfirmedDTO.newPassword()));
         personRepository.save(person);
         codeRepository.delete(code);
        }

    private String generateCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
