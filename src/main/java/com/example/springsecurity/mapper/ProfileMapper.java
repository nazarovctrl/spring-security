package com.example.springsecurity.mapper;

import com.example.springsecurity.entity.ProfileEntity;
import com.example.springsecurity.dto.auth.RegistrationDTO;
import com.example.springsecurity.dto.profile.ProfileResponseDTO;
import com.example.springsecurity.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileResponseDTO map(ProfileEntity entity) {
        return ProfileResponseDTO.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .isVisible(entity.getIsVisible())
                .createdDate(entity.getCreatedDate()).build();
    }

    public ProfileEntity map(RegistrationDTO dto) {
        return new ProfileEntity().toBuilder()
                .fullName(dto.getFirstname())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.USER).build();
    }
}
