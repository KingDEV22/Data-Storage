package com.data.organization.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@NoArgsConstructor
@Data
public class DataRecord {
    @Id
    private String dataRecordId;
    private LocalDateTime createDate = LocalDateTime.now();
    private String metaDataId;
    private Map<String, Object> data = new HashMap<>();
    public DataRecord(String metaDataId, Map<String, Object> data) {
        this.metaDataId = metaDataId;
        this.data = data;
    }   
}
