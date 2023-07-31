package com.data.organization.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;


@Data
@Document(collation = "question")
public class Question {
    private String qname;
    private String qlabel;
    private String qtype;
    private String fId;

    public Question(String qname, String qlabel, String qtype, String fId) {
        this.qname = qname;
        this.qlabel = qlabel;
        this.qtype = qtype;
        this.fId = fId;
    }

}
