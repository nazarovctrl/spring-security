package com.example.springsecurity.repository;


import com.example.springsecurity.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {

    Optional<ProfileEntity> findByEmail(String email);
}
