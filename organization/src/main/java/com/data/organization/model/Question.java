package com.data.organization.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;


@Data
@Document
public class Question {
    @Id
    private String questionId;
    private String qname;
    private String qlabel;
    private String qtype;
    private String metaDataId;
    public Question( String qname, String qlabel, String qtype, String metaDataId) {
        this.qname = qname;
        this.qlabel = qlabel;
        this.qtype = qtype;
        this.metaDataId = metaDataId;
    }
}
