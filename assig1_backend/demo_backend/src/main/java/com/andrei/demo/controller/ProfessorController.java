package com.andrei.demo.controller;

import com.andrei.demo.config.DuplicateEmailException;
import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Professor;
import com.andrei.demo.model.ProfessorCreateDTO;
import com.andrei.demo.service.ProfessorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/professor")
public class ProfessorController {
    private final ProfessorService professorService;

    @GetMapping()
    public List<Professor> getProfessors() {
        return professorService.getProfessors();
    }

    @GetMapping("/{uuid}")
    public Professor getProfessorById(@PathVariable UUID uuid) throws ValidationException {
        return professorService.getProfessorById(uuid);
    }

    @GetMapping("/email/{email}")
    public Professor getProfessorByEmail(@PathVariable String email) throws ValidationException {
        return professorService.getProfessorByEmail(email);
    }

    @PostMapping()
    public Professor addProfessor(@Valid @RequestBody ProfessorCreateDTO professorDTO) throws DuplicateEmailException {
        return professorService.addProfessor(professorDTO);
    }

    @PutMapping("/{uuid}")
    public Professor updateProfessor(@PathVariable UUID uuid, @RequestBody Professor professor)
            throws ValidationException {
        return professorService.updateProfessor(uuid, professor);
    }

    @DeleteMapping("/{uuid}")
    public void deleteProfessor(@PathVariable UUID uuid) {
        professorService.deleteProfessor(uuid);
    }
}
