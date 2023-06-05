package com.example.springsecurity.service;


import com.example.springsecurity.UserEntity;
import com.example.springsecurity.config.JwtService;
import com.example.springsecurity.dto.auth.LoginDTO;
import com.example.springsecurity.dto.auth.LoginResponseDTO;
import com.example.springsecurity.dto.auth.RegistrationDTO;
import com.example.springsecurity.dto.profile.ProfileResponseDTO;
import com.example.springsecurity.enums.UserRole;
import com.example.springsecurity.exp.EmailAlreadyExistsException;
import com.example.springsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public ProfileResponseDTO registration(RegistrationDTO dto) {


        Optional<UserEntity> exists = repository.findByEmail(dto.getEmail());
        if (exists.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        var user = UserEntity.builder()
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(UserRole.ROLE_USER)
                .build();

        repository.save(user);


        return getDTO(user);

    }


    public ProfileResponseDTO getDTO(UserEntity entity) {

        ProfileResponseDTO profileDTO = new ProfileResponseDTO();
        profileDTO.setId(entity.getId());
        profileDTO.setFirstname(entity.getFirstname());
        profileDTO.setLastname(entity.getLastname());
        profileDTO.setEmail(entity.getEmail());

        profileDTO.setRole(entity.getRole());
        profileDTO.setVisible(entity.getVisible());
        profileDTO.setCreatedDate(entity.getCreatedDate());

        return profileDTO;
    }


    public LoginResponseDTO login(LoginDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );
        var user = repository.findByEmail(dto.getEmail()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return LoginResponseDTO.builder()
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
