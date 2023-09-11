package com.data.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuestionDTO {
    private String qId;
    private String qname;
    private String qtype;
    private String qlabel;
}
