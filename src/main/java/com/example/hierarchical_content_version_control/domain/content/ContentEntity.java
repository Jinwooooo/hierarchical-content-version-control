package com.example.hierarchical_content_version_control.domain.content;

import com.example.hierarchical_content_version_control.domain.content.enums.*;
import lombok.AccessLevel;                                                                                                                                  
import lombok.Getter;                                                                                                                                       
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;                                                                                                                             
import org.springframework.data.annotation.Id;                                                                                                              
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class ContentEntity {
    @Id
    private ObjectId id;

    private ObjectId parentId;              // id == parentId for root (i.e. HQ contents)

    private ObjectId vehicleId;

    private ObjectId divisionId;

    private String version;                 // {HQ.NSC.DISTRIBUTOR} : 0.0.0

    private Integer modifiedCtr;            // used to check DRAFT <-> PUBLISHED content for update condition

    private ContentStatus status;

    private ContentType type;

    private String name;

    private String code;

    private boolean isDeleted;              // soft delete flag

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private ContentEntity(
        ObjectId id,
        ObjectId parentId,
        ObjectId vehicleId,
        ObjectId divisionId,
        String version,
        Integer modifiedCtr,
        ContentStatus status,
        ContentType type,
        String name,
        String code,
        boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.parentId = parentId;
        this.vehicleId = vehicleId;
        this.divisionId = divisionId;
        this.version = version;
        this.modifiedCtr = modifiedCtr;
        this.status = status;
        this.type = type;
        this.name = name;
        this.code = code;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ContentEntity createHQDraft(
        ObjectId vehicleId,
        ObjectId divisionId,
        ContentType type,
        String name,
        String code
    ) {
        validateNotNull("vehicleId", vehicleId);
        validateNotNull("divisionId", divisionId);
        validateNotNull("type", type);
        validateNotNull("name", name);
        validateNotNull("code", code);
        
        ObjectId id = new ObjectId();
        LocalDateTime now = LocalDateTime.now();

        return new ContentEntity(
            id,
            id,
            vehicleId,
            divisionId,
            "0.0.0",
            0,
            ContentStatus.DRAFT,
            type,
            name,
            code,
            false,
            now,
            now
        );
    }

    public static ContentEntity createDraft(
        ContentEntity parentPublished,
        ObjectId divisionId
    ) {
        validateNotNull("parentPublished", parentPublished);
        validateNotNull("divisionId", divisionId);

        if (parentPublished.getStatus() != ContentStatus.PUBLISHED) {
            throw new IllegalArgumentException("Can only add from PUBLISHED content");
        }

        ObjectId id = new ObjectId();
        LocalDateTime now = LocalDateTime.now();

        return new ContentEntity(
            id,
            parentPublished.getId(),
            parentPublished.getVehicleId(),
            divisionId,
            parentPublished.getVersion(),
            0,
            ContentStatus.DRAFT,
            parentPublished.getType(),
            parentPublished.getName(),
            parentPublished.getCode(),
            false,
            now,
            now
        );
    }

    public ContentEntity publish(int levelIdx) {
        if (this.status != ContentStatus.DRAFT) {
            throw new IllegalStateException("Can only publish from DRAFT");
        }

        String newVersion = incrementVersion(this.version, levelIdx);
        LocalDateTime now = LocalDateTime.now();

        return new ContentEntity(
            new ObjectId(),
            this.parentId,
            this.vehicleId,
            this.divisionId,
            newVersion,
            this.modifiedCtr,
            ContentStatus.PUBLISHED,
            this.type,
            this.name,
            this.code,
            false,
            now,
            now
        );
    }

    public void modify() {
        if (this.status != ContentStatus.DRAFT) {
            throw new IllegalStateException("Can only modify DRAFT");
        }

        this.modifiedCtr++;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        if (this.isDeleted) {
            throw new IllegalStateException("Content is already deleted");
        }

        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void restore() {
        if (!this.isDeleted) {
            throw new IllegalStateException("Content is not deleted");
        }

        this.isDeleted = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isRoot() {
        return this.id != null && this.id.equals(this.parentId);
    }

    private static String incrementVersion(String version, int levelIdx) {
        int[] parts = parseVersion(version);

        parts[levelIdx]++;

        return parts[0] + "." + parts[1] + "." + parts[2];
    }

    private static int[] parseVersion(String version) {
        String[] parts = version.split("\\.");

        return new int[] {
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1]),
            Integer.parseInt(parts[2])
        };
    }

    private static void validateNotBlank(String key, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(key + " cannot be null or blank");
        }
    }

    private static void validateNotNull(String key, Object value) {
        if (value == null) {
            throw new IllegalArgumentException(key + " cannot be null");
        }
    }
}
