package com.example.springsecurity.dto.auth;

import com.example.springsecurity.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRegistrationDTO {

    @NotBlank(message = "Name required")
    private String name;

    @NotBlank(message = "Surname required")
    private String surname;

    @Email(message = "Email required")
    private String email;

    @Size(min = 8, message = "Password required")
    private String password;

    @NotBlank(message = "Role required")
    private UserRole role;

}
