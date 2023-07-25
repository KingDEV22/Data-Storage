package com.data.organization.model;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AppUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;
    private String id;
    private String email;

    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Boolean locked;
    private Boolean enabled;

    public static AppUserDetails build(OrgUser user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRoles().toString());

        return new AppUserDetails(
                user.getUserId(),
                user.getEmail(),
                user.getPassword(),
                Arrays.asList(authority),
                user.getLocked(),
                user.getEnabled());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {

        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {

        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return !locked;
    }

    @Override
    public boolean isEnabled() {

        return enabled;
    }

}
