package com.andrei.demo.service;

import com.andrei.demo.config.DuplicateEmailException;
import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Professor;
import com.andrei.demo.model.ProfessorCreateDTO;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.repository.ProfessorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProfessorService {
    private final ProfessorRepository professorRepository;
    private final PersonRepository personRepository;

    public List<Professor> getProfessors() {
        return professorRepository.findAll();
    }

    public Professor getProfessorById(UUID uuid) throws ValidationException {
        return professorRepository.findById(uuid).orElseThrow(
                () -> new ValidationException("Professor with id " + uuid + " not found"));
    }

    public Professor getProfessorByEmail(String email) throws ValidationException {
        return professorRepository.findByEmail(email).orElseThrow(
                () -> new ValidationException("Professor with email " + email + " not found"));
    }

    public Professor addProfessor(ProfessorCreateDTO professorDTO) throws DuplicateEmailException {
        if (personRepository.existsByEmail(professorDTO.getEmail())) {
            throw new DuplicateEmailException("Email " + professorDTO.getEmail() + " already exists");
        }

        Professor professor = new Professor();
        professor.setName(professorDTO.getName());
        professor.setPassword(professorDTO.getPassword());
        professor.setAge(professorDTO.getAge());
        professor.setEmail(professorDTO.getEmail());
        professor.setDepartment(professorDTO.getDepartment());
        professor.setAcademicRank(professorDTO.getAcademicRank());
        return professorRepository.save(professor);
    }

    public Professor updateProfessor(UUID uuid, Professor professor) throws ValidationException, DuplicateEmailException {
        Professor existingProfessor = professorRepository.findById(uuid).orElseThrow(
                () -> new ValidationException("Professor with id " + uuid + " not found"));

        String newEmail = professor.getEmail();
        if (newEmail != null && !newEmail.equals(existingProfessor.getEmail())
                && personRepository.existsByEmailAndIdNot(newEmail, existingProfessor.getId())) {
            throw new DuplicateEmailException("Email " + newEmail + " already exists");
        }

        existingProfessor.setName(professor.getName());
        existingProfessor.setPassword(professor.getPassword());
        existingProfessor.setAge(professor.getAge());
        existingProfessor.setEmail(professor.getEmail());
        existingProfessor.setDepartment(professor.getDepartment());
        existingProfessor.setAcademicRank(professor.getAcademicRank());
        return professorRepository.save(existingProfessor);
    }

    public void deleteProfessor(UUID uuid) {
        professorRepository.deleteById(uuid);
    }
}
