package com.data.organization.dto;

import java.util.List;

import com.data.organization.model.Question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class FormDataUpdateDTO {
    private List<Question> questions;
    private String formName;
    private String newFormName;
}
