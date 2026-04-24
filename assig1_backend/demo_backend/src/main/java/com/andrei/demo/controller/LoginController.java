package com.andrei.demo.controller;

import com.andrei.demo.model.dto.LoginRequestDTO;
import com.andrei.demo.model.dto.LoginResponseDTO;
import com.andrei.demo.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@AllArgsConstructor
@CrossOrigin
public class LoginController {
    private final LoginService securityService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponseDTO = securityService.login(loginRequestDTO.email(), loginRequestDTO.password());
        if(loginResponseDTO.success()) {
            return ResponseEntity.ok(loginResponseDTO);
        } else {
            return ResponseEntity.status(UNAUTHORIZED).body(loginResponseDTO);
        }
    }
}