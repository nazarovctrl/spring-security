package com.example.springsecurity.security.profile;

import com.example.springsecurity.entity.ProfileEntity;
import com.example.springsecurity.enums.Status;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class ProfileDetails implements UserDetails {
    private final ProfileEntity profile;

    public ProfileDetails(@NotNull ProfileEntity profile) {
        this.profile = profile;
    }

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(profile.getRole().name()));
    }

    @Override
    public String getPassword() {
        return profile.getPassword();
    }

    @Override
    public String getUsername() {
        return profile.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !profile.getStatus().equals(Status.BLOCKED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return profile.getIsVisible();
    }
}
