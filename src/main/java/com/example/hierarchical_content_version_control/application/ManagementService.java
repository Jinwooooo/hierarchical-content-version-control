package com.example.hierarchical_content_version_control.application;

import com.example.hierarchical_content_version_control.domain.content.dto.ContentDTO;
import com.example.hierarchical_content_version_control.domain.content.enums.*;
import com.example.hierarchical_content_version_control.domain.division.dto.DivisionDTO;
import org.bson.types.ObjectId;
                                                                                                                                                            
import java.util.List;

public interface ManagementService {
    /**
     * ********************
     * Division ops
     * ********************
     */

    /**
     * CREATE ops
     */
    DivisionDTO createHQ(String code, String name);
    DivisionDTO createNSC(ObjectId parentDivisionId, String code, String name);
    DivisionDTO createDistributor(
        ObjectId parentDivisionId,
        String code, 
        String name
    );
    
    /**
     * READ ops
     */
    DivisionDTO findDivisionById(ObjectId divisionId);
    List<DivisionDTO> findChildDivisions(ObjectId parentDivisionId);

    /**
     * ********************
     * Content ops
     * ********************
     */

    /**
     * CREATE ops
     */
    ContentDTO createDraft(
        ObjectId vehicleId,
        ObjectId divisionId,
        ContentType type,
        String name,
        String code
    );
    ContentDTO addFromParent(ObjectId parentId, ObjectId divisionId);
    ContentDTO publish(ObjectId draftId);
    ContentDTO leafPublish(ObjectId draftId);

    /**
     * UPDATE ops
     */
    ContentDTO modifyDraft(
        ObjectId draftId, 
        String name, 
        String code
    );
    
    /**
     * READ ops
     */
    ContentDTO findContentById(ObjectId id);
    ContentDTO findDraft(ObjectId vehicleId, ObjectId divisionId);
    ContentDTO findPublished(ObjectId vehicleId, ObjectId divisionId);
    List<ContentDTO> findAllDraftByDivision(ObjectId divisionId);
    List<ContentDTO> findAllPublishedByDivision(ObjectId divisionId);
    List<ContentDTO> findAvailable(ObjectId divisionId);

    /**
     * (soft) DELETE ops
     */
    void softDeleteContent(ObjectId id);
    void restoreContent(ObjectId id);
}
