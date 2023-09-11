package com.data.organization.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document
public class MetaData {
    @Id
    private String metaDataId;
    private String link;
    private String name;
    private String type;
    private LocalDateTime createDate = LocalDateTime.now();
    private String orgId;

    public MetaData(String link, String name, String type, String orgId) {
        this.link = link;
        this.name = name;
        this.type = type;
        this.orgId = orgId;
    }
}
