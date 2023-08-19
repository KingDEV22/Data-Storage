package com.data.organization.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Document
@EqualsAndHashCode(exclude = "qid")
public class Question {
    @Id
    private String qid;
    private String qname;
    private String qlabel;
    private String qtype;
    private String fid;
    public Question( String qname, String qlabel, String qtype, String fid) {
        this.qname = qname;
        this.qlabel = qlabel;
        this.qtype = qtype;
        this.fid = fid;
    }
}
