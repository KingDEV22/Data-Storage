package com.data.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerRequest {
    private String name;
    private String value;
    private String label; 
    private String type;
}
