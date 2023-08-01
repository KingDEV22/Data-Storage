package com.data.organization.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormMetaDataResponse {
    private String link;
    private String name;
    private LocalDateTime createDate;
}
