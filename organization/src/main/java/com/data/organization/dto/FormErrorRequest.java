package com.data.organization.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FormErrorRequest {
    private List<QA> questions;
    private String formName;
    private String country;
    private String state;
    private String url;

}
