package com.example.hierarchical_content_version_control.api.v1.external.dto;

import lombok.Data;

@Data
public class ContentAddRequest {
    private String parentPublishedId;
    private String divisionId;
}
