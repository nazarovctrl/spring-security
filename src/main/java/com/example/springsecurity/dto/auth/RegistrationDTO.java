package com.example.springsecurity.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDTO {
    @NotBlank(message = "Firstname required")
    private String firstname;

    @NotBlank(message = "Lastname required")
    private String lastname;

    @Email(message = "Email required")
    private String email;

    @NotBlank
    private String password;
}
