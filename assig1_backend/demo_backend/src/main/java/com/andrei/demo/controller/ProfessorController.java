package com.andrei.demo.controller;

import com.andrei.demo.config.exceptions.DuplicateEmailException;
import com.andrei.demo.config.exceptions.ValidationException;
import com.andrei.demo.controller.annotations.IsAdmin;
import com.andrei.demo.controller.annotations.IsStudent;
import com.andrei.demo.model.Professor;
import com.andrei.demo.model.dto.ProfessorCreateDTO;
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

    @IsStudent
    @GetMapping()
    public List<Professor> getProfessors() {
        return professorService.getProfessors();
    }

    @IsStudent
    @GetMapping("/{uuid}")
    public Professor getProfessorById(@PathVariable UUID uuid) throws ValidationException {
        return professorService.getProfessorById(uuid);
    }

    @IsStudent
    @GetMapping("/email/{email}")
    public Professor getProfessorByEmail(@PathVariable String email) throws ValidationException {
        return professorService.getProfessorByEmail(email);
    }

    @IsAdmin
    @PostMapping()
    public Professor addProfessor(@Valid @RequestBody ProfessorCreateDTO professorDTO) throws DuplicateEmailException {
        return professorService.addProfessor(professorDTO);
    }

    @IsAdmin
    @PutMapping("/{uuid}")
    public Professor updateProfessor(@PathVariable UUID uuid, @RequestBody Professor professor)
            throws ValidationException {
        return professorService.updateProfessor(uuid, professor);
    }

    @IsAdmin
    @DeleteMapping("/{uuid}")
    public void deleteProfessor(@PathVariable UUID uuid) {
        professorService.deleteProfessor(uuid);
    }
}
