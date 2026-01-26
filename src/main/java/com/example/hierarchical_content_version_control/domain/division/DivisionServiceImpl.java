package com.example.hierarchical_content_version_control.domain.division;

import com.example.hierarchical_content_version_control.domain.division.dto.DivisionDTO;                                                                    
import com.example.hierarchical_content_version_control.domain.division.enums.DivisionLevel;                                                                
import lombok.RequiredArgsConstructor;                                                                                                                      
import org.bson.types.ObjectId;                                                                                                                             
import org.springframework.stereotype.Service;                                                                                                              
                                                                                                                                                            
import java.util.List; 

@Service
@RequiredArgsConstructor
class DivisionServiceImpl implements DivisionService {
    private final DivisionRepository divisionRepository;

    @Override
    public DivisionDTO createHQ(String code, String name) {
        DivisionEntity createdEntity = DivisionEntity.createHQ(code, name);
        DivisionEntity savedEntity = divisionRepository.save(createdEntity);

        return toDTO(savedEntity);
    }

    @Override
    public DivisionDTO createNSC(
        ObjectId parentDivisionId,
        String code,
        String name
    ) {
        DivisionEntity parentEntity = findEntityByIdOrThrow(parentDivisionId);
        validateParentLevel(parentEntity, DivisionLevel.HQ, DivisionLevel.NSC);

        DivisionEntity createdEntity = DivisionEntity.createNSC(parentDivisionId, code, name);
        DivisionEntity savedEntity = divisionRepository.save(createdEntity);

        return toDTO(savedEntity);
    }

    @Override
    public DivisionDTO createDistributor(
        ObjectId parentDivisionId,
        String code,
        String name
    ) {
        DivisionEntity parentEntity = findEntityByIdOrThrow(parentDivisionId);
        validateParentLevel(parentEntity, DivisionLevel.NSC, DivisionLevel.DISTRIBUTOR);

        DivisionEntity createdEntity = DivisionEntity.createDistributor(parentDivisionId, code, name);
        DivisionEntity savedEntity = divisionRepository.save(createdEntity);

        return toDTO(savedEntity);
    }

    @Override
    public DivisionDTO findById(ObjectId id) {
        DivisionEntity entity = findEntityByIdOrThrow(id);

        return toDTO(entity);
    }

    @Override
    public List<DivisionDTO> findAllByLevel(DivisionLevel level) {
        return divisionRepository.findAllByLevel(level)
                .stream()
                .map(entity -> toDTO(entity))
                .toList();
    }

    @Override
    public List<DivisionDTO> findAllByParentDivisionId(ObjectId parentDivisionId) {
        return divisionRepository.findAllByParentDivisionId(parentDivisionId)
                .stream()
                .map(entity -> toDTO(entity))
                .toList();
    }

    @Override
    public void deleteById(ObjectId id) {
        if (!divisionRepository.existsById(id)) {
            throw new IllegalArgumentException("Division not found: " + id);
        }

        divisionRepository.deleteById(id);
    }

    private DivisionEntity findEntityByIdOrThrow(ObjectId id) {                                                                                             
        return divisionRepository.findById(id)                                                                                                              
                .orElseThrow(() -> new IllegalArgumentException("Division not found: " + id));                                                              
    }                                                                                                                                                       
                                                                                                                                                            
    private void validateParentLevel(DivisionEntity parent, DivisionLevel parentLevel, DivisionLevel currentLevel) {                                                
        if (parent.getLevel() != parentLevel) {                                                                                                           
            throw new IllegalArgumentException(                                                                                                             
                    currentLevel + " parent must be " + parentLevel + ", got " + parent.getLevel()                                                           
            );                                                                                                                                              
        }                                                                                                                                                   
    }                                                                                                                                                       
                                                                                                                                                            
    private DivisionDTO toDTO(DivisionEntity entity) {                                                                                                      
        return new DivisionDTO(                                                                                                                             
            entity.getId(),                                                                                                                             
            entity.getParentDivisionId(),                                                                                                               
            entity.getLevel(),                                                                                                                          
            entity.getCode(),                                                                                                                     
            entity.getName()                                                                                                                            
        );                                                                                                                                                  
    }
}
