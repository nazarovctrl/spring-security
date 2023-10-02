package com.example.springsecurity.mapper;

import com.example.springsecurity.entity.ProfileEntity;
import com.example.springsecurity.dto.auth.RegistrationDTO;
import com.example.springsecurity.dto.profile.ProfileResponseDTO;
import com.example.springsecurity.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileResponseDTO map(ProfileEntity entity) {
        ProfileResponseDTO profileDTO = new ProfileResponseDTO();
        profileDTO.setId(entity.getId());
        profileDTO.setFirstname(entity.getFirstname());
        profileDTO.setLastname(entity.getLastname());
        profileDTO.setEmail(entity.getEmail());

        profileDTO.setRole(entity.getRole());
        profileDTO.setVisible(entity.getIsVisible());
        profileDTO.setCreatedDate(entity.getCreatedDate());

        return profileDTO;
    }

    public ProfileEntity map(RegistrationDTO dto) {
        return ProfileEntity.builder()
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.ROLE_USER)
                .build();
    }
}
