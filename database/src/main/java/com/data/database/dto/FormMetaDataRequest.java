package com.data.database.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FormMetaDataRequest {
    private List<QuestionsRequest> questions;
    private String link;
    private String name;
    private String orgId;

}
