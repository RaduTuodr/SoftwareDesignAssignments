package com.andrei.demo.controller.unit;

import com.andrei.demo.controller.PersonController;
import com.andrei.demo.model.Person;
import com.andrei.demo.model.dto.PasswordChangeConfirmedDTO;
import com.andrei.demo.model.dto.PasswordChangeRequestDTO;
import com.andrei.demo.model.dto.PersonCreateDTO;
import com.andrei.demo.service.PasswordResetService;
import com.andrei.demo.service.PersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonControllerTests {
    @Mock
    private PersonService service;
    @Mock
    private PasswordResetService passwordResetService;
    @InjectMocks
    private PersonController controller;

    @Test
    void allMethodsDelegate() throws Exception {
        UUID id = UUID.randomUUID();
        Person person = new Person();
        PersonCreateDTO dto = new PersonCreateDTO();
        PasswordChangeRequestDTO passwordChangeRequestDTO = new PasswordChangeRequestDTO("0712345678");
        PasswordChangeConfirmedDTO passwordChangeConfirmedDTO =
                new PasswordChangeConfirmedDTO("OldPass123!", "NewPass123!", "123456");

        when(service.getPeople()).thenReturn(List.of(person));
        when(service.getPersonById(id)).thenReturn(person);
        when(service.getPersonByEmail("a@b.com")).thenReturn(person);
        when(service.addPerson(dto)).thenReturn(person);
        when(service.updatePerson(id, person)).thenReturn(person);
        when(service.partialUpdatePerson(id, person)).thenReturn(person);

        assertEquals(1, controller.getPeople().size());
        assertEquals(person, controller.getPersonById(id));
        assertEquals(person, controller.getPersonByEmail("a@b.com"));
        assertEquals(person, controller.addPerson(dto));
        assertEquals(person, controller.updatePerson(id, person));
        assertEquals(person, controller.partialUpdatePerson(id, person));
        controller.handlePasswordChangeRequest(id, passwordChangeRequestDTO);
        controller.confirmPasswordChange(id, passwordChangeConfirmedDTO);
        controller.deletePerson(id);

        verify(passwordResetService).handlePasswordChangeRequest(id, passwordChangeRequestDTO);
        verify(passwordResetService).confirmPasswordReset(id, passwordChangeConfirmedDTO);
        verify(service).deletePerson(id);
    }
}
