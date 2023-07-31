package com.data.organization.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class Form {
    private String fId;

    private String link;

    private String name;

    private String orgId;

    public Form(String link, String name, String orgId) {
        this.link = link;
        this.name = name;
        this.orgId = orgId;
    }

}
