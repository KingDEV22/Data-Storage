package com.data.organization.dto;

import java.util.Set;

import com.data.organization.model.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class RegistrationRequest {
    private final String name;
    private final String email;
    private final String password;
    private final String phoneNo;
    private final String address;
    private final String country;
    private final Set<Role> roles;
}
