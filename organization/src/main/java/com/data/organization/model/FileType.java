package com.data.organization.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum FileType {
    CSV("text/csv"),
    EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    private String type;
}
