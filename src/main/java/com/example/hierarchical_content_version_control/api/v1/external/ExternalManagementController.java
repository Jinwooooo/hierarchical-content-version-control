package com.example.hierarchical_content_version_control.api.v1.external;

import com.example.hierarchical_content_version_control.api.v1.external.dto.*;
import com.example.hierarchical_content_version_control.application.ManagementService;
import com.example.hierarchical_content_version_control.domain.content.dto.ContentDTO;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/external/management/contents")
@RequiredArgsConstructor
public class ExternalManagementController {

    private final ManagementService managementService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContentDTO createDraft(@RequestBody ContentCreateRequest request) {
        return managementService.createDraft(
            new ObjectId(request.getVehicleId()),
            new ObjectId(request.getDivisionId()),
            request.getType(),
            request.getName(),
            request.getCode()
        );
    }

    @PostMapping("/add-from-parent")
    @ResponseStatus(HttpStatus.CREATED)
    public ContentDTO addFromParent(@RequestBody ContentAddRequest request) {
        return managementService.addFromParent(
            new ObjectId(request.getParentPublishedId()),
            new ObjectId(request.getDivisionId())
        );
    }

    @PostMapping("/{id}/publish")
    public ContentDTO publish(@PathVariable String id) {
        return managementService.publish(new ObjectId(id));
    }

    @PostMapping("/{id}/leaf-publish")
    public ContentDTO leafPublish(@PathVariable String id) {
        return managementService.leafPublish(new ObjectId(id));
    }

    @PatchMapping("/{id}")
    public ContentDTO modifyDraft(@PathVariable String id, @RequestBody ContentModifyRequest request) {
        return managementService.modifyDraft(
            new ObjectId(id),
            request.getName(),
            request.getCode()
        );
    }

    @GetMapping("/{id}")
    public ContentDTO getContent(@PathVariable String id) {
        return managementService.findContentById(new ObjectId(id));
    }

    @GetMapping("/draft")
    public ContentDTO getDraft(@RequestParam String vehicleId, @RequestParam String divisionId) {
        return managementService.findDraft(new ObjectId(vehicleId), new ObjectId(divisionId));
    }

    @GetMapping("/published")
    public ContentDTO getPublished(@RequestParam String vehicleId, @RequestParam String divisionId) {
        return managementService.findPublished(new ObjectId(vehicleId), new ObjectId(divisionId));
    }

    @GetMapping("/division/{divisionId}/drafts")
    public List<ContentDTO> getDraftsByDivision(@PathVariable String divisionId) {
        return managementService.findAllDraftByDivision(new ObjectId(divisionId));
    }

    @GetMapping("/division/{divisionId}/published")
    public List<ContentDTO> getPublishedByDivision(@PathVariable String divisionId) {
        return managementService.findAllPublishedByDivision(new ObjectId(divisionId));
    }

    @GetMapping("/division/{divisionId}/available")
    public List<ContentDTO> getAvailable(@PathVariable String divisionId) {
        return managementService.findAvailable(new ObjectId(divisionId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContent(@PathVariable String id) {
        managementService.softDeleteContent(new ObjectId(id));
    }

    @PostMapping("/{id}/restore")
    public void restoreContent(@PathVariable String id) {
        managementService.restoreContent(new ObjectId(id));
    }
}
