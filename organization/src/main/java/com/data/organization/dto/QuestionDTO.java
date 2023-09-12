package com.data.organization.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuestionDTO {
    @NotNull
    private String questionId;
    @NotNull
    private String qname;
    @NotNull
    private String qtype;
    @NotNull
    private String qlabel;
}
