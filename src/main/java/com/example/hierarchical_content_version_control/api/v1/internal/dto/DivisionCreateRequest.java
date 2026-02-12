package com.example.hierarchical_content_version_control.api.v1.internal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DivisionCreateRequest {
    private String parentDivisionId; // String to allow simple JSON input, converted to ObjectId in controller
    private String code;
    private String name;
}
