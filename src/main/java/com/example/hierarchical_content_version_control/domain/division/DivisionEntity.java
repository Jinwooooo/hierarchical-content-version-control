package com.example.hierarchical_content_version_control.domain.division;

import com.example.hierarchical_content_version_control.domain.division.enums.DivisionLevel;
import lombok.AccessLevel;                                                                                                                                  
import lombok.Getter;                                                                                                                                       
import lombok.NoArgsConstructor;                                                                                                                            
import org.bson.types.ObjectId;                                                                                                                             
import org.springframework.data.annotation.Id;                                                                                                              
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "divisions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class DivisionEntity {
    @Id
    private ObjectId id;

    private ObjectId parentDivisionId;

    private DivisionLevel level;

    private String code;

    private String name;

    private DivisionEntity(
        ObjectId id,
        ObjectId parentDivisionId,
        DivisionLevel level,
        String code,
        String name
    ) {
        this.id = id;
        this.parentDivisionId = parentDivisionId;
        this.level = level;
        this.code = code;
        this.name = name;
    }

    public static DivisionEntity createHQ(
        String code, 
        String name
    ) {
        validateNotBlank("code", code);
        validateNotBlank("name", name);

        ObjectId id = new ObjectId();

        return new DivisionEntity(id, id, DivisionLevel.HQ, code, name);
    }

    public static DivisionEntity createNSC(
        ObjectId parentDivisionId,
        String code,
        String name
    ) {
        validateParent(parentDivisionId);
        validateNotBlank("code", code);
        validateNotBlank("name", name);

        return new DivisionEntity(new ObjectId(), parentDivisionId, DivisionLevel.NSC, code, name);
    }

    public static DivisionEntity createDistributor(
        ObjectId parentDivisionId,
        String code,
        String name
    ) {
        validateParent(parentDivisionId);
        validateNotBlank("code", code);
        validateNotBlank("name", name);

        return new DivisionEntity(new ObjectId(), parentDivisionId, DivisionLevel.DISTRIBUTOR, code, name);
    }

    public boolean isRoot() {
        return this.id != null && this.id.equals(this.parentDivisionId);
    }

    /**
     * Helper Functions
     */
    private static void validateNotBlank(String key, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(key + " cannot be null or blank");
        }
    }

    private static void validateParent(ObjectId parentDivisionId) {
        if (parentDivisionId == null) {
            throw new IllegalArgumentException("parentDivisionId cannot be null");
        }
    }
}
