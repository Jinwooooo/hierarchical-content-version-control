package com.example.hierarchical_content_version_control.domain.content;                                                                                    
                                                                                                                                                              
import com.example.hierarchical_content_version_control.domain.content.dto.ContentDTO;                                                                      
import com.example.hierarchical_content_version_control.domain.content.enums.*;                                                                   
import org.bson.types.ObjectId;                                                                                                                             
                                                                                                                                                            
import java.util.List;                                                                                                                                      
                                                                                                                                                              
public interface ContentService { 
    /**
     * CREATE ops
     */
    ContentDTO createHQDraft(
        ObjectId vehicleId,
        ObjectId divisionId,
        ContentType type,
        String name,
        String code
    );
    ContentDTO addFromParent(ObjectId parentId, ObjectId divisionId);
    ContentDTO publish(ObjectId draftId, int levelIdx);

    /**
     * UPDATE ops
     */
    ContentDTO modify(
        ObjectId draftId, 
        String name, 
        String code
    );

    /**
     * READ ops
     */
    ContentDTO findById(ObjectId id);
    ContentDTO findDraft(ObjectId vehicleId, ObjectId divisionId);
    ContentDTO findPublished(ObjectId vehicleId, ObjectId divisionId);
    List<ContentDTO> findAllDraftByDivision(ObjectId divisionId);
    List<ContentDTO> findAllPublishedByDivision(ObjectId divisionId);
    
    /**
     * (soft) DELETE ops
     */
    void softDelete(ObjectId id);
    void restore(ObjectId id);

    /**
     * (performance) READ ops
     */
    boolean draftExists(ObjectId vehicleId, ObjectId divisionId);
    boolean publishedExists(ObjectId vehicleId, ObjectId divisionId);
}
