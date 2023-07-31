package com.data.organization.model;

import java.util.Set;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "org_user")
public class OrgUser {
    @Id
    private String id;
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
    public OrgUser(String orgId, String name, String password, String email, String address, String country,
            Set<Role> roles, Boolean locked, Boolean enabled) {
        this.orgId = orgId;
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
