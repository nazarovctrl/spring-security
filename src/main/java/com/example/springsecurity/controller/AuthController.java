package com.example.springsecurity.controller;

import com.example.springsecurity.security.ProfileDetailsService;
import com.example.springsecurity.security.JwtService;
import com.example.springsecurity.dto.auth.LoginDTO;
import com.example.springsecurity.dto.auth.LoginResponseDTO;
import com.example.springsecurity.dto.auth.RegistrationDTO;
import com.example.springsecurity.dto.profile.ProfileResponseDTO;
import com.example.springsecurity.security.ResponseGenerator;
import com.example.springsecurity.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authorization Controller", description = "This controller for authorization")
public class AuthController {

    private final AuthService service;
    private final JwtService jwtService;
    private final ProfileDetailsService userDetailService;
    private final ResponseGenerator responseGenerator;


    @Operation(summary = "Method for registration", description = "This method used to create a user")
    @PostMapping("/registration")
    private ResponseEntity<ProfileResponseDTO> registration(@Valid @RequestBody RegistrationDTO dto) {
        log.info("Registration : email {}, name {}", dto.getEmail(), dto.getFirstname());
        ProfileResponseDTO result = service.registration(dto);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Method for authorization", description = "This method used for Login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO dto) {
        log.info(" Login :  email {} ", dto.getEmail());
        LoginResponseDTO response = service.login(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Method for refresh token", description = "This method used for refresh token")
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            String refreshToken = authorizationHeader.substring("Bearer ".length());
            if (jwtService.isTokenExpired(refreshToken)) {
                String tokenExpiredMessage = jwtService.getTokenExpiredMessage(refreshToken);
                responseGenerator.generateError(tokenExpiredMessage, response);
                return;
            }

            String username = jwtService.extractRefreshTokenUsername(refreshToken);
            UserDetails userDetails = userDetailService.loadUserByUsername(username);

            String accessToken = jwtService.generateAccessToken(userDetails.getUsername());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", refreshToken);
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        }
    }
}
