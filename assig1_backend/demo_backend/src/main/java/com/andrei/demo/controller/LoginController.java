package com.andrei.demo.controller;

import com.andrei.demo.model.dto.LoginRequestDTO;
import com.andrei.demo.model.dto.LoginResponseDTO;
import com.andrei.demo.model.dto.RegisterRequestDTO;
import com.andrei.demo.model.dto.RegisterResponseDTO;
import com.andrei.demo.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/auth")
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponseDTO = loginService.login(loginRequestDTO);
        if(loginResponseDTO.success()) {
            return ResponseEntity.ok(loginResponseDTO);
        } else {
            return ResponseEntity.status(UNAUTHORIZED).body(loginResponseDTO);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        RegisterResponseDTO registerResponseDTO = loginService.register(registerRequestDTO);
        if(registerResponseDTO.success()) {
            return ResponseEntity.ok(registerResponseDTO);
        } else {
            return ResponseEntity.status(BAD_REQUEST).body(registerResponseDTO);
        }
    }
}