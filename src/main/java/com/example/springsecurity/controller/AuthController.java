package com.example.springsecurity.controller;

import com.example.springsecurity.config.CustomUserDetailService;
import com.example.springsecurity.config.JwtService;
import com.example.springsecurity.dto.auth.LoginDTO;
import com.example.springsecurity.dto.auth.LoginResponseDTO;
import com.example.springsecurity.dto.auth.RegistrationDTO;
import com.example.springsecurity.dto.profile.ProfileResponseDTO;
import com.example.springsecurity.exp.JWTTokenExpiredException;
import com.example.springsecurity.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authorization Controller", description = "This controller for authorization")
public class AuthController {

    private final AuthService service;
    private final JwtService jwtService;
    private final CustomUserDetailService userDetailService;

    public AuthController(AuthService service, JwtService jwtService, CustomUserDetailService userDetailService) {
        this.service = service;
        this.jwtService = jwtService;
        this.userDetailService = userDetailService;
    }


    @Operation(summary = "Method for registration", description = "This method used to create a user")
    @PostMapping("/registration")
    private ResponseEntity<ProfileResponseDTO> registration(@Valid @RequestBody RegistrationDTO dto) {
        log.info("Registration : email {}, name {}", dto.getEmail(), dto.getFirstname());
        ProfileResponseDTO result = service.registration(dto);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Method for authorization", description = "This method used for Login")
    @PostMapping("/authorization")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO dto) {
        log.info(" Login :  email {} ", dto.getEmail());
        LoginResponseDTO response = service.login(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Method for refresh token", description = "This method used for refresh token")
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader("AUTHORIZATION");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                jwtService.isTokenExpired(refreshToken);

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(jwtService.getRefreshTokenSignInKey())
                        .build()
                        .parseClaimsJws(refreshToken)
                        .getBody();
                Function<Claims, String> claimsResolver = Claims::getSubject;
                String username = claimsResolver.apply(claims);
                UserDetails userDetails = userDetailService.loadUserByUsername(username);

                String accessToken = jwtService.generateAccessToken(userDetails);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (JWTTokenExpiredException e) {
                response.setHeader("error", e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
                return;
            }


        }

    }

}
