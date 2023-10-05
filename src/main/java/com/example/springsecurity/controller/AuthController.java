package com.example.springsecurity.controller;

import com.example.springsecurity.dto.auth.LoginDTO;
import com.example.springsecurity.dto.auth.LoginResponseDTO;
import com.example.springsecurity.dto.auth.RegistrationDTO;
import com.example.springsecurity.dto.profile.ProfileResponseDTO;
import com.example.springsecurity.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authorization Controller", description = "This controller for authorization")
public class AuthController {
    private final AuthService service;

    @Operation(summary = "Method for registration", description = "This method used to create a user")
    @PostMapping("/registration")
    private ResponseEntity<ProfileResponseDTO> registration(@Valid @RequestBody RegistrationDTO dto) {
        log.info("Registration : email {}, name {}", dto.getEmail(), dto.getFirstname());
        ProfileResponseDTO result = service.registration(dto);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Method for authorization", description = "This method used for Login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        log.info(" Login :  email {} ", dto.getEmail());
        LoginResponseDTO response = service.login(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Method for refresh token", description = "This method used for refresh token")
    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return service.refreshToken(request);
    }
}
