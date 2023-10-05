package com.example.springsecurity.service;

import com.example.springsecurity.entity.ProfileEntity;
import com.example.springsecurity.security.jwt.JwtService;
import com.example.springsecurity.dto.auth.LoginDTO;
import com.example.springsecurity.dto.auth.LoginResponseDTO;
import com.example.springsecurity.dto.auth.RegistrationDTO;
import com.example.springsecurity.dto.profile.ProfileResponseDTO;
import com.example.springsecurity.exp.EmailAlreadyExistsException;
import com.example.springsecurity.mapper.ProfileMapper;
import com.example.springsecurity.repository.ProfileRepository;
import com.example.springsecurity.security.profile.ProfileDetailsService;
import com.example.springsecurity.util.MD5;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final ProfileRepository profileRepository;
    private final ProfileDetailsService profileDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final ProfileMapper profileMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public ProfileResponseDTO registration(RegistrationDTO dto) {
        Optional<ProfileEntity> exists = profileRepository.findByEmail(dto.getEmail());
        if (exists.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        dto.setPassword(passwordEncoder.encode(MD5.md5(dto.getPassword())));
        ProfileEntity profile = profileMapper.map(dto);

        profileRepository.save(profile);

        return profileMapper.map(profile);
    }


    public LoginResponseDTO login(LoginDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        MD5.md5(dto.getPassword())));

        ProfileEntity profile = profileRepository.findByEmail(dto.getEmail()).orElseThrow();

        String accessToken = jwtService.generateAccessToken(profile.getEmail());
        String refreshToken = jwtService.generateRefreshToken(profile.getEmail());
        return LoginResponseDTO.builder()
                .role(profile.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> body = new HashMap<>();

        String refreshToken = authorizationHeader.substring("Bearer ".length());
        if (jwtService.isTokenExpired(refreshToken)) {
            String tokenExpiredMessage = jwtService.getTokenExpiredMessage(refreshToken);
            body.put("error", tokenExpiredMessage);
            return ResponseEntity.status(SC_FORBIDDEN).body(body);
        }

        String username = jwtService.extractRefreshTokenUsername(refreshToken);
        UserDetails userDetails = profileDetailsService.loadUserByUsername(username);
        String accessToken = jwtService.generateAccessToken(userDetails.getUsername());

        body.put("accessToken", accessToken);
        return ResponseEntity.ok(body);
    }
}