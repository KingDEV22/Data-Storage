package com.data.organization.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FormDeleteDTO {
    private List<String> questionIds;
    private String formName;

}
