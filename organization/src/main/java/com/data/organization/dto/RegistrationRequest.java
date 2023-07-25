package com.data.organization.dto;

import lombok.Data;

@Data
public class RegistrationRequest {
    private final String name;
    private final String email;
    private final String password;
    private final String phoneNo;
    private final String Address;
    private final String country;
    private final Boolean admin;
}
