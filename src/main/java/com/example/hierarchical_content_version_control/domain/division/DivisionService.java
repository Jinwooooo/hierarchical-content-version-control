package com.example.hierarchical_content_version_control.domain.division;

import com.example.hierarchical_content_version_control.domain.division.dto.DivisionDTO;
import com.example.hierarchical_content_version_control.domain.division.enums.DivisionLevel;
import org.bson.types.ObjectId;                                                                                                                             

import java.util.List;

public interface DivisionService {
    DivisionDTO createHQ(String code, String name);

    DivisionDTO createNSC(ObjectId parentDivisionId, String code, String name);

    DivisionDTO createDistributor(ObjectId parentDivisionId, String code, String name);

    DivisionDTO findById(ObjectId id);

    List<DivisionDTO> findAllByLevel(DivisionLevel level);

    List<DivisionDTO> findAllByParentDivisionId(ObjectId parentDivisionId);

    void deleteById(ObjectId id);
}
