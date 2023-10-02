package com.example.springsecurity.service;

import com.example.springsecurity.entity.ProfileEntity;
import com.example.springsecurity.security.JwtService;
import com.example.springsecurity.dto.auth.LoginDTO;
import com.example.springsecurity.dto.auth.LoginResponseDTO;
import com.example.springsecurity.dto.auth.RegistrationDTO;
import com.example.springsecurity.dto.profile.ProfileResponseDTO;
import com.example.springsecurity.exp.EmailAlreadyExistsException;
import com.example.springsecurity.mapper.ProfileMapper;
import com.example.springsecurity.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileMapper profileMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public ProfileResponseDTO registration(RegistrationDTO dto) {
        Optional<ProfileEntity> exists = profileRepository.findByEmail(dto.getEmail());
        if (exists.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        ProfileEntity profile = profileMapper.map(dto);

        profileRepository.save(profile);

        return profileMapper.map(profile);
    }


    public LoginResponseDTO login(LoginDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );
        ProfileEntity profile = profileRepository.findByEmail(dto.getEmail()).orElseThrow();

        String accessToken = jwtService.generateAccessToken(profile.getEmail());
        String refreshToken = jwtService.generateRefreshToken(profile.getEmail());
        return LoginResponseDTO.builder()
                .role(profile.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}