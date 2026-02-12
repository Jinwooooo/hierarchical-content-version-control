package com.example.hierarchical_content_version_control.api.v1.internal;

import com.example.hierarchical_content_version_control.api.v1.internal.dto.DivisionCreateRequest;
import com.example.hierarchical_content_version_control.application.ManagementService;
import com.example.hierarchical_content_version_control.domain.division.dto.DivisionDTO;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/internal/divisions")
@RequiredArgsConstructor
public class InternalDivisionController {

    private final ManagementService managementService;

    @PostMapping("/hq")
    @ResponseStatus(HttpStatus.CREATED)
    public DivisionDTO createHQ(@RequestBody DivisionCreateRequest request) {
        return managementService.createHQ(request.getCode(), request.getName());
    }

    @PostMapping("/nsc")
    @ResponseStatus(HttpStatus.CREATED)
    public DivisionDTO createNSC(@RequestBody DivisionCreateRequest request) {
        validateParentId(request.getParentDivisionId());
        return managementService.createNSC(
            new ObjectId(request.getParentDivisionId()), 
            request.getCode(), 
            request.getName()
        );
    }

    @PostMapping("/distributor")
    @ResponseStatus(HttpStatus.CREATED)
    public DivisionDTO createDistributor(@RequestBody DivisionCreateRequest request) {
        validateParentId(request.getParentDivisionId());
        return managementService.createDistributor(
            new ObjectId(request.getParentDivisionId()), 
            request.getCode(), 
            request.getName()
        );
    }

    @GetMapping("/{id}")
    public DivisionDTO getDivision(@PathVariable String id) {
        return managementService.findDivisionById(new ObjectId(id));
    }

    @GetMapping("/{id}/children")
    public List<DivisionDTO> getChildren(@PathVariable String id) {
        return managementService.findChildDivisions(new ObjectId(id));
    }

    private void validateParentId(String parentId) {
        if (parentId == null || parentId.isBlank()) {
            throw new IllegalArgumentException("parentDivisionId is required for this operation");
        }
    }
}
