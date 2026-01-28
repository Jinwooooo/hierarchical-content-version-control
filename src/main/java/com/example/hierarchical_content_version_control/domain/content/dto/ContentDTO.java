package com.example.hierarchical_content_version_control.domain.content.dto;

import com.example.hierarchical_content_version_control.domain.content.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ContentDTO {
    private ObjectId id;
    private ObjectId parentId;
    private ObjectId vehicleId;
    private ObjectId divisionId;
    private String version;
    private Integer modifiedCtr;
    private ContentStatus status;
    private ContentType type;
    private String name;
    private String code;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
