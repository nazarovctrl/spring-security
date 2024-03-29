package com.example.springsecurity.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    @Email(message = "Email required")
    private String email;

    @Size(min = 8, message = "Password required")
    private String password;
}
