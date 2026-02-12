package com.example.hierarchical_content_version_control.api.v1.external.dto;

import com.example.hierarchical_content_version_control.domain.content.enums.ContentType;
import lombok.Data;

@Data
public class ContentCreateRequest {
    private String vehicleId;
    private String divisionId;
    private ContentType type;
    private String name;
    private String code;
}
