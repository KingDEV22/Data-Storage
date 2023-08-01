package com.data.organization.model;



import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class Form {
    private String fId;
    private String link;
    private String name;
    private LocalDateTime createDate;
    private String orgId;
    public Form(String fId, String link, String name, String orgId) {
        this.fId = fId;
        this.link = link;
        this.name = name;
        this.orgId = orgId;
         this.createDate = LocalDateTime.now();
    }
}
