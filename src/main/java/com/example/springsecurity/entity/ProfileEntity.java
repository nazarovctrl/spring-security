package com.example.springsecurity.entity;

import com.example.springsecurity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "profile")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String firstname;

    @Column
    private String lastname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private Boolean isVisible = true;

    @Column
    @CreationTimestamp
    private LocalDateTime createdDate;
}
