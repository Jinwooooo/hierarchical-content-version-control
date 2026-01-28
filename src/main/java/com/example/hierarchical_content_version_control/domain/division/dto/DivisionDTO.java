package com.example.hierarchical_content_version_control.domain.division.dto;

import com.example.hierarchical_content_version_control.domain.division.enums.DivisionLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@Builder
@AllArgsConstructor
public class DivisionDTO {
    private ObjectId id;
    private ObjectId parentDivisionId;
    private DivisionLevel level;
    private String code;
    private String name;
}
