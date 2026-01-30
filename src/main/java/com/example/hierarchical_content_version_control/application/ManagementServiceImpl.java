package com.example.hierarchical_content_version_control.application;

import com.example.hierarchical_content_version_control.domain.content.ContentService;
import com.example.hierarchical_content_version_control.domain.content.dto.ContentDTO;
import com.example.hierarchical_content_version_control.domain.content.enums.*;
import com.example.hierarchical_content_version_control.domain.division.DivisionService;
import com.example.hierarchical_content_version_control.domain.division.dto.DivisionDTO;
import com.example.hierarchical_content_version_control.domain.division.enums.*;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
                                                                                                                                                              
import java.util.List;

@Service
@RequiredArgsConstructor
class ManagementServiceImpl implements ManagementService {
    private final ContentService contentService;
    private final DivisionService divisionService;

    /**
     * ********************
     * Division ops
     * ********************
     */

    /**
     * CREATE ops
     */
    @Override
    public DivisionDTO createHQ(String code, String name) {
        return divisionService.createHQ(code, name);
    }

    @Override
    public DivisionDTO createNSC(
        ObjectId parentDivisionId,
        String code,
        String name
    ) {
        return divisionService.createNSC(parentDivisionId, code, name);
    }

    @Override
    public DivisionDTO createDistributor(
        ObjectId parentDivisionId,
        String code,
        String name
    ) {
        return divisionService.createDistributor(parentDivisionId, code, name);
    }

    /**
     * READ ops
     */
    @Override
    public DivisionDTO findDivisionById(ObjectId divisionId) {
        return divisionService.findById(divisionId);
    }

    @Override
    public List<DivisionDTO> findChildDivisions(ObjectId parentDivisionId) {
        return divisionService.findAllByParentDivisionId(parentDivisionId);
    }

    /**
     * ********************
     * Content ops
     * ********************
     */

    /**
     * CREATE ops
     */
    @Override
    public ContentDTO createDraft(
        ObjectId vehicleId,
        ObjectId divisionId,
        ContentType type,
        String name,
        String code
    ) {
        DivisionDTO division = divisionService.findById(divisionId);

        if (division.getLevel() != DivisionLevel.HQ) {
            throw new IllegalArgumentException("Only HQ can create new content. Use addFromParent for " + division.getLevel());
        }

        return contentService.createHQDraft(vehicleId, divisionId, type, name, code);
    }

    @Override
    public ContentDTO addFromParent(ObjectId parentPublishedId, ObjectId divisionId) {
        return contentService.addFromParent(parentPublishedId, divisionId);
    }

    @Override
    public ContentDTO publish(ObjectId draftId) {
        ContentDTO draft = contentService.findById(draftId);
        DivisionDTO division = divisionService.findById(draft.getDivisionId());

        int levelIdx = getLevelIndex(division.getLevel());

        if (contentService.publishedExists(draft.getVehicleId(), draft.getDivisionId())) {
            throw new IllegalStateException("Published content already exists, Use republish to overwrite");
        }

        return contentService.publish(draftId, levelIdx);
    }

    @Override
    public ContentDTO leafPublish(ObjectId draftId) {
        ContentDTO draft = contentService.findById(draftId);
        DivisionDTO division = divisionService.findById(draft.getDivisionId());

        if (division.getLevel() != DivisionLevel.DISTRIBUTOR) {
            throw new IllegalStateException("only DISTRIBUTOR can overwrite publish");
        }

        int levelIdx = getLevelIndex(division.getLevel());

        return contentService.leafPublish(draftId, levelIdx);
    }

    /**
     * UPDATE ops
     */
    @Override
    public ContentDTO modifyDraft(ObjectId draftId, String name, String code) {
        return contentService.modify(draftId, name, code);
    }

    /**
     * READ ops
     */
    @Override
    public ContentDTO findContentById(ObjectId contentId) {
        return contentService.findById(contentId);
    }

    @Override
    public ContentDTO findDraft(ObjectId vehicleId, ObjectId divisionId) {
        return contentService.findDraft(vehicleId, divisionId);
    }

    @Override
    public ContentDTO findPublished(ObjectId vehicleId, ObjectId divisionId) {
        return contentService.findPublished(vehicleId, divisionId);
    }

    @Override
    public List<ContentDTO> findAllDraftByDivision(ObjectId divisionId) {
        return contentService.findAllPublishedByDivision(divisionId);
    }

    @Override
    public List<ContentDTO> findAllPublishedByDivision(ObjectId divisionId) {
        return contentService.findAllPublishedByDivision(divisionId);
    }

    @Override
    public List<ContentDTO> findAvailable(ObjectId divisionId) {
        DivisionDTO division = divisionService.findById(divisionId);

        if (division.getLevel() == DivisionLevel.HQ) {
            throw new IllegalArgumentException("HQ has no parent division");
        }

        ObjectId parentDivisionId = division.getParentDivisionId();

        return contentService.findAllPublishedByDivision(parentDivisionId);
    }

    /**
     * (soft) DELETE ops
     */
    @Override
    public void softDeleteContent(ObjectId id) {
        contentService.softDelete(id);
    }

    @Override
    public void restoreContent(ObjectId id) {
        contentService.restore(id);
    }

    /**
     * ********************
     * helper functions
     * ********************
     */
    private int getLevelIndex(DivisionLevel level) {
        return switch (level) {
            case HQ -> 0;
            case NSC -> 1;
            case DISTRIBUTOR -> 2;
        };
    }

    private boolean isRootContent(ContentDTO content) {
        return content.getId().equals(content.getParentId());
    }

    private int[] parseVersion(String version) {
        String[] parts = version.split("\\.");

        return new int[] {
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1]),
            Integer.parseInt(parts[2])
        };
    }
}
