package com.example.springsecurity.dto.profile;

import com.example.springsecurity.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponseDTO {
    private Integer id;
    private String fullName;
    private String email;
    private Role role;
    private Boolean isVisible;
    private LocalDateTime createdDate;
}
