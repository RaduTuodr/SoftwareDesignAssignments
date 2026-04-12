package com.andrei.demo.service;

import com.andrei.demo.config.DuplicateEmailException;
import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Person;
import com.andrei.demo.model.PersonCreateDTO;
import com.andrei.demo.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTests {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    private Person person;

    @BeforeEach
    void setUp() {
        person = new Person();
        person.setId(UUID.randomUUID());
        person.setName("John");
        person.setAge(30);
        person.setEmail("john@example.com");
        person.setPassword("Pass123!");
    }

    @Test
    void getPeopleReturnsAll() {
        when(personRepository.findAll()).thenReturn(List.of(person));

        List<Person> result = personService.getPeople();

        assertEquals(1, result.size());
        assertEquals(person, result.getFirst());
    }

    @Test
    void addPersonThrowsWhenEmailExists() {
        PersonCreateDTO dto = new PersonCreateDTO();
        dto.setEmail("john@example.com");
        when(personRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> personService.addPerson(dto));
        verify(personRepository, never()).save(any());
    }

    @Test
    void addPersonSavesMappedEntity() {
        PersonCreateDTO dto = new PersonCreateDTO();
        dto.setName("Alice");
        dto.setAge(22);
        dto.setEmail("alice@example.com");
        dto.setPassword("Strong123!");

        when(personRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

        try {
            Person result = personService.addPerson(dto);

            assertEquals("Alice", result.getName());
            assertEquals("alice@example.com", result.getEmail());
        } catch (ValidationException e) {
            fail("ValidationException should not be thrown");
        }
    }

    @Test
    void updatePersonThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(personRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> personService.updatePerson(id, new Person()));
    }

    @Test
    void updatePersonThrowsWhenEmailAlreadyUsed() {
        UUID id = person.getId();
        Person patch = new Person();
        patch.setEmail("taken@example.com");

        when(personRepository.findById(id)).thenReturn(Optional.of(person));
        when(personRepository.existsByEmailAndIdNot("taken@example.com", id)).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> personService.updatePerson(id, patch));
    }

    @Test
    void updatePersonUpdatesEntity() throws ValidationException {
        UUID id = person.getId();
        Person patch = new Person();
        patch.setName("Jane");
        patch.setAge(21);
        patch.setEmail("jane@example.com");
        patch.setPassword("Other123!");

        when(personRepository.findById(id)).thenReturn(Optional.of(person));
        when(personRepository.existsByEmailAndIdNot("jane@example.com", id)).thenReturn(false);
        when(personRepository.save(person)).thenReturn(person);

        Person result = personService.updatePerson(id, patch);

        assertEquals("Jane", result.getName());
        assertEquals("jane@example.com", result.getEmail());
    }

    @Test
    void updatePerson2UpdatesWhenFound() throws ValidationException {
        UUID id = person.getId();
        Person patch = new Person();
        patch.setName("MapName");

        when(personRepository.findById(id)).thenReturn(Optional.of(person));
        when(personRepository.save(person)).thenReturn(person);

        Person result = personService.updatePerson2(id, patch);

        assertEquals("MapName", result.getName());
    }

    @Test
    void updatePerson2ThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(personRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> personService.updatePerson2(id, new Person()));
    }

    @Test
    void partialUpdatePersonUpdatesOnlyProvidedFields() throws ValidationException {
        UUID id = person.getId();
        Person patch = new Person();
        patch.setName("Partial");
        patch.setEmail("partial@example.com");

        when(personRepository.findById(id)).thenReturn(Optional.of(person));
        when(personRepository.existsByEmailAndIdNot("partial@example.com", id)).thenReturn(false);
        when(personRepository.save(person)).thenReturn(person);

        Person result = personService.partialUpdatePerson(id, patch);

        assertEquals("Partial", result.getName());
        assertEquals(30, result.getAge());
        assertEquals("partial@example.com", result.getEmail());
    }

    @Test
    void partialUpdatePersonThrowsWhenEmailAlreadyUsed() {
        UUID id = person.getId();
        Person patch = new Person();
        patch.setEmail("taken@example.com");

        when(personRepository.findById(id)).thenReturn(Optional.of(person));
        when(personRepository.existsByEmailAndIdNot("taken@example.com", id)).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> personService.partialUpdatePerson(id, patch));
    }

    @Test
    void partialUpdatePersonThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(personRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> personService.partialUpdatePerson(id, new Person()));
    }

    @Test
    void deletePersonDelegatesToRepository() {
        UUID id = UUID.randomUUID();

        personService.deletePerson(id);

        verify(personRepository).deleteById(id);
    }

    @Test
    void getPersonByEmailThrowsWhenMissing() {
        when(personRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> personService.getPersonByEmail("missing@example.com"));
    }

    @Test
    void getPersonByEmailReturnsValue() {
        when(personRepository.findByEmail(person.getEmail())).thenReturn(Optional.of(person));

        Person result = personService.getPersonByEmail(person.getEmail());

        assertEquals(person, result);
    }

    @Test
    void getPersonByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(personRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> personService.getPersonById(id));
    }

    @Test
    void getPersonByIdReturnsValue() {
        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));

        Person result = personService.getPersonById(person.getId());

        assertEquals(person, result);
    }
}
