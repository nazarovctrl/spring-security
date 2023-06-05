package com.example.springsecurity.dto.auth;

import com.example.springsecurity.enums.UserRole;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private UserRole role;
    private String accessToken;
    private String refreshToken;

}
