package com.example.hierarchical_content_version_control.domain.content;

import com.example.hierarchical_content_version_control.domain.content.enums.*;                                                               
import org.bson.types.ObjectId;                                                                                                                             
import org.springframework.data.mongodb.repository.MongoRepository;                                                                                         
import org.springframework.stereotype.Repository;                                                                                                           
                                                                                                                                                            
import java.util.List;                                                                                                                                      
import java.util.Optional;

@Repository
interface ContentRepository extends MongoRepository<ContentEntity, ObjectId> {
    /**
     * Single record queries (active)
     */
    Optional<ContentEntity> findByIdAndIsDeletedFalse(ObjectId id);
    
    Optional<ContentEntity> findByVehicleIdAndDivisionIdAndStatusAndIsDeletedFalse(
        ObjectId vehicleId,
        ObjectId divisionId,
        ContentStatus status
    );

    /**
     * List record queries (active)
     */
    List<ContentEntity> findByDivisionIdAndIsDeletedFalse(ObjectId divisionId);

    List<ContentEntity> findByDivisionIdAndStatusAndIsDeletedFalse(
        ObjectId divisionId,
        ContentStatus status
    );

    List<ContentEntity> findByVehicleIdAndIsDeletedFalse(ObjectId vehicleId);

    List<ContentEntity> findByVehicleIdAndStatusAndIsDeletedFalse(
        ObjectId vehicleId,
        ContentStatus status
    );

    List<ContentEntity> findByParentIdAndIsDeletedFalse(ObjectId parentId);

    List<ContentEntity> findByParentIdAndStatusAndIsDeletedFalse(
        ObjectId parentId,
        ContentStatus status
    );

    List<ContentEntity> findByTypeAndIsDeletedFalse(ContentType type);

    /**
     * Existence checks (active)
     */
    boolean existsByVehicleIdAndDivisionIdAndStatusAndIsDeletedFalse(
        ObjectId vehicleId,
        ObjectId divisionId,
        ContentStatus status
    );
}
