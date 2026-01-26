package com.example.hierarchical_content_version_control.domain.division;

import com.example.hierarchical_content_version_control.domain.division.enums.DivisionLevel;                                                                
import org.bson.types.ObjectId;                                                                                                                             
import org.springframework.data.mongodb.repository.MongoRepository;                                                                                         
import org.springframework.stereotype.Repository;                                                                                                           
                                                                                                                                                            
import java.util.List;

@Repository
interface DivisionRepository extends MongoRepository<DivisionEntity, ObjectId> {
    List<DivisionEntity> findAllByLevel(DivisionLevel level);
    List<DivisionEntity> findAllByParentDivisionId(ObjectId parentDivisionId);
}
