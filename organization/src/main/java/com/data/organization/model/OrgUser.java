package com.data.organization.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Document(collection = "org_users")
@AllArgsConstructor
@ToString
@Getter
@Setter
public class OrgUser {
    @Id
    private String userId;
    private String name;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String email;
    private String address;
    private String country;
    private Set<Role> roles = new HashSet<>();
    @JsonIgnore
    private Boolean locked;
    @JsonIgnore
    private Boolean enabled;

}
