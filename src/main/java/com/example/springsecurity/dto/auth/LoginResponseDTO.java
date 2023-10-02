package com.example.springsecurity.dto.auth;

import com.example.springsecurity.enums.Role;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private Role role;
    private String accessToken;
    private String refreshToken;

}
