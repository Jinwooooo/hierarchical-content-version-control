package com.example.hierarchical_content_version_control.domain.content;

import com.example.hierarchical_content_version_control.domain.content.dto.ContentDTO;                                                                      
import com.example.hierarchical_content_version_control.domain.content.enums.*;

import lombok.RequiredArgsConstructor;                                                                                                                      
import org.bson.types.ObjectId;                                                                                                                             
import org.springframework.stereotype.Service;                                                                                                              
                                                                                                                                                            
import java.util.List;

@Service
@RequiredArgsConstructor
class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;

    /**
     * CREATE ops
     */
    @Override
    public ContentDTO createHQDraft(
        ObjectId vehicleId,
        ObjectId divisionId,
        ContentType type,
        String name,
        String code
    ) {
        if (draftExists(vehicleId, divisionId)) {
            throw new IllegalStateException("Draft already exists for this vehicle and division");
        }

        ContentEntity draft = ContentEntity.createHQDraft(vehicleId, divisionId, type, name, code);
        ContentEntity saved = contentRepository.save(draft);

        return toDTO(saved);
    }

    @Override
    public ContentDTO addFromParent(ObjectId parentPublishedId, ObjectId divisionId) {
        ContentEntity parentPublished = findEntityByIdOrThrow(parentPublishedId);

        if (parentPublished.getStatus() != ContentStatus.PUBLISHED) {
            throw new IllegalArgumentException("Can only create draft from PUBLISHED content");
        }

        if (draftExists(parentPublished.getVehicleId(), divisionId)) {
            throw new IllegalStateException("Draft already exists for this vehicle and division");
        }

        ContentEntity draft = ContentEntity.createDraft(parentPublished, divisionId);
        ContentEntity saved = contentRepository.save(draft);

        return toDTO(saved);
    }

    @Override
    public ContentDTO publish(ObjectId draftId, int levelIdx) {
        ContentEntity draft = findEntityByIdOrThrow(draftId);

        if (draft.getStatus() != ContentStatus.DRAFT) {
            throw new IllegalStateException("Can only publish DRAFT content");
        }

        ContentEntity published = draft.publish(levelIdx);
        ContentEntity saved = contentRepository.save(published);

        return toDTO(saved);
    }

    /**
     * UPDATE ops
     */
    @Override
    public ContentDTO modify(
        ObjectId draftId, 
        String name, 
        String code
    ) {
        ContentEntity entity = findEntityByIdOrThrow(draftId);

        if (entity.getStatus() != ContentStatus.DRAFT) {
            throw new IllegalStateException("Can only modify DRAFT content");
        }

        entity.modify(name, code);
        ContentEntity updated = contentRepository.save(entity);

        return toDTO(updated);
    }

    /**
     * READ ops
     */
    @Override
    public ContentDTO findById(ObjectId id) {
        ContentEntity entity = findEntityByIdOrThrow(id);

        return toDTO(entity);
    }

    @Override
    public ContentDTO findDraft(ObjectId vehicleId, ObjectId divisionId) {
        ContentEntity entity = contentRepository.findByVehicleIdAndDivisionIdAndStatusAndIsDeletedFalse(
            vehicleId, 
            divisionId, 
            ContentStatus.DRAFT
        ).orElseThrow(() -> new IllegalArgumentException("DRAFT content not found"));

        return toDTO(entity);
    }

    @Override
    public ContentDTO findPublished(ObjectId vehicleId, ObjectId divisionId) {
        ContentEntity entity = contentRepository.findByVehicleIdAndDivisionIdAndStatusAndIsDeletedFalse(
            vehicleId, 
            divisionId, 
            ContentStatus.PUBLISHED
        ).orElseThrow(() -> new IllegalArgumentException("PUBLISHED content not found"));

        return toDTO(entity);
    }

    @Override
    public List<ContentDTO> findAllDraftByDivision(ObjectId divisionId) {
        return contentRepository.findByDivisionIdAndStatusAndIsDeletedFalse(divisionId, ContentStatus.DRAFT)
                .stream()
                .map(entity -> toDTO(entity))
                .toList();
    }

    @Override
    public List<ContentDTO> findAllPublishedByDivision(ObjectId divisionId) {
        return contentRepository.findByDivisionIdAndStatusAndIsDeletedFalse(divisionId, ContentStatus.PUBLISHED)
                .stream()
                .map(entity -> toDTO(entity))
                .toList();
    }

    /**
     * (soft) DELETE ops
     */
    @Override
    public void softDelete(ObjectId id) {
        ContentEntity entity = findEntityByIdOrThrow(id);
        entity.softDelete();
        contentRepository.save(entity);
    }

    @Override
    public void restore(ObjectId id) {
        ContentEntity entity = contentRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Content not found: " + id));
        entity.restore();
        contentRepository.save(entity);
    }

    /**
     * (performance) READ ops
     */
    @Override
    public boolean draftExists(ObjectId vehicleId, ObjectId divisionId) {
        return contentRepository.existsByVehicleIdAndDivisionIdAndStatusAndIsDeletedFalse(
            vehicleId,
            divisionId, 
            ContentStatus.DRAFT
        );
    }

    @Override
    public boolean publishedExists(ObjectId vehicleId, ObjectId divisionId) {
        return contentRepository.existsByVehicleIdAndDivisionIdAndStatusAndIsDeletedFalse(
            vehicleId, 
            divisionId, 
            ContentStatus.PUBLISHED
        );
    }

    /**
     * helper functions
     */
    private ContentEntity findEntityByIdOrThrow(ObjectId id) {
        return contentRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("Content not found: " + id));
    }

    private ContentDTO toDTO(ContentEntity entity) {
        return new ContentDTO(
            entity.getId(),
            entity.getParentId(),
            entity.getVehicleId(),
            entity.getDivisionId(),
            entity.getVersion(),
            entity.getModifiedCtr(),
            entity.getStatus(),
            entity.getType(),
            entity.getName(),
            entity.getCode(),
            entity.isDeleted(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
