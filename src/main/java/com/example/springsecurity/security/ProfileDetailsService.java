package com.example.springsecurity.security;

import com.example.springsecurity.entity.ProfileEntity;
import com.example.springsecurity.repository.ProfileRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ProfileDetailsService implements UserDetailsService {
    private final ProfileRepository profileRepository;

    public ProfileDetailsService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ProfileEntity> optional = profileRepository.findByEmail(username);
        if (optional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new ProfileDetails(optional.get());
    }
}
