package com.example.springsecurity.dto.profile;

import com.example.springsecurity.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponseDTO {

    private Integer id;
    private String firstname;
    private String lastname;

    private String email;

    private UserRole role;
    private Boolean visible;

    private String photoId;
    private LocalDateTime createdDate;


}
