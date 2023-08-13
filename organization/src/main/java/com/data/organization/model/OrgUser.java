package com.data.organization.model;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "org_user")
public class OrgUser {
    @Id
    private String orgId;
    private String name;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String email;
    private String address;
    private String country;
    private Set<Role> roles;
    @JsonIgnore
    private Boolean locked;
    @JsonIgnore
    private Boolean enabled;
    public OrgUser(String name, String password, String email, String address, String country,
            Set<Role> roles, Boolean locked, Boolean enabled) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.address = address;
        this.country = country;
        this.roles = roles;
        this.locked = locked;
        this.enabled = enabled;
    }

   

}
