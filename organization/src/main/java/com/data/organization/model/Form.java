package com.data.organization.model;



import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Document
public class Form {
    private String fId;
    private String link;
    private String name;
    private String orgId;

}
