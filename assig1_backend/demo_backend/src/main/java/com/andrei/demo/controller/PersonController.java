package com.andrei.demo.controller;

import com.andrei.demo.config.exceptions.DuplicateEmailException;
import com.andrei.demo.config.exceptions.ValidationException;
import com.andrei.demo.controller.annotations.IsAdmin;
import com.andrei.demo.controller.annotations.IsStudent;
import com.andrei.demo.controller.annotations.IsVisitor;
import com.andrei.demo.model.dto.PasswordChangeConfirmedDTO;
import com.andrei.demo.model.dto.PasswordChangeRequestDTO;
import com.andrei.demo.model.dto.PersonCreateDTO;
import com.andrei.demo.service.PasswordResetService;
import com.andrei.demo.service.PersonService;
import com.andrei.demo.model.Person;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;

    private final PasswordResetService passwordResetService;

    @IsStudent
    @GetMapping()
    public List<Person> getPeople() {
        return personService.getPeople();
    }

    @IsStudent
    @GetMapping("/{uuid}")
    public Person getPersonById(@PathVariable UUID uuid) {
        return personService.getPersonById(uuid);
    }

    @IsStudent
    @GetMapping("/email/{email}")
    public Person getPersonByEmail(@PathVariable String email) {
        return personService.getPersonByEmail(email);
    }

    @IsAdmin
    @PostMapping("")
    public Person addPerson(@Valid @RequestBody PersonCreateDTO personDTO)
            throws DuplicateEmailException
    { return personService.addPerson(personDTO); }

    @IsStudent
    @PutMapping("/{uuid}")
    public Person updatePerson(@PathVariable UUID uuid, @Valid @RequestBody Person person) throws ValidationException { return personService.updatePerson(uuid, person); }

    @IsVisitor
    @PutMapping("/{uuid}/password/request")
    public void handlePasswordChangeRequest(@PathVariable UUID uuid, @Valid @RequestBody PasswordChangeRequestDTO passwordChangeRequestDTO) throws ValidationException { passwordResetService.handlePasswordChangeRequest(uuid, passwordChangeRequestDTO); }

    @IsVisitor
    @PutMapping("/{uuid}/password/confirm")
    public void confirmPasswordChange(@PathVariable UUID uuid, @Valid @RequestBody PasswordChangeConfirmedDTO passwordChangeConfirmedDTO) throws ValidationException { passwordResetService.confirmPasswordReset(uuid, passwordChangeConfirmedDTO); }

    @IsStudent
    @PatchMapping("/{uuid}")
    public Person partialUpdatePerson(@PathVariable UUID uuid, @RequestBody Person person) throws ValidationException, DuplicateEmailException { return personService.partialUpdatePerson(uuid, person); }

    @IsAdmin
    @DeleteMapping("/{uuid}")
    public void deletePerson(@PathVariable UUID uuid) {
        personService.deletePerson(uuid);
    }
}
