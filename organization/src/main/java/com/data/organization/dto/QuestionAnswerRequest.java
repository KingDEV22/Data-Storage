package com.data.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerRequest {
    private String qname;
    private String qvalue;
    private String qlabel; 
    private String qtype;
}
