package com.data.organization.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class Question {
        @Id
        private String qId;
        private String question;
        private String type;
        private String fId;
        public Question(String question, String type, String f_id) {
            this.question = question;
            this.type = type;
            this.fId = f_id;
        }

        
}
