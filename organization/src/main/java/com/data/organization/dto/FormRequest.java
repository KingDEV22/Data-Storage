package com.data.organization.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FormRequest {
    private List<QuestionRequest> questions;
    private String formName;
    private String country;
}
