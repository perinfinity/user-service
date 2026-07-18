package com.perinfinity.auth_api.controller;

import com.perinfinity.auth_api.dtos.OrgProfileDto;
import com.perinfinity.auth_api.dtos.UpdateOrgProfileRequest;
import com.perinfinity.auth_api.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    /**
     * T-061 — Public organization profile lookup by ID.
     * No authentication required.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrgProfileDto> getOrgById(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.getOrgById(id));
    }

    /**
     * T-061 — Authenticated organization profile update.
     * Only accessible by users with ORGANIZATION role.
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ResponseEntity<OrgProfileDto> updateMyProfile(
            Authentication authentication,
            @RequestBody UpdateOrgProfileRequest request) {

        String email = authentication.getName();
        return ResponseEntity.ok(organizationService.updateMyProfile(email, request));
    }
}
