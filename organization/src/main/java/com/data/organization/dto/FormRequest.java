package com.data.organization.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FormRequest {
    @NotNull
    private List<QuestionDTO> questions;
    @NotNull
    private String formName;
}
