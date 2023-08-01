package com.data.database.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Document(collation = "answer_data")
public class Answer {
    private String q_id;
    private String answer;
    private String f_id;
}
