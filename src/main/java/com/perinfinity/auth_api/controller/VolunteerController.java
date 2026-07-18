package com.perinfinity.auth_api.controller;

import com.perinfinity.auth_api.dtos.UpdateProfileRequest;
import com.perinfinity.auth_api.dtos.VolunteerProfileDto;
import com.perinfinity.auth_api.entities.Role;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.exceptions.UserNotFoundException;
import com.perinfinity.auth_api.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/volunteers")
@RequiredArgsConstructor
public class VolunteerController {

    private final UserRepository userRepository;

    /**
     * T-020 — Public profile lookup by username.
     * Exposes only non-sensitive fields (no email, no password).
     */
    @GetMapping("/{username}")
    public ResponseEntity<VolunteerProfileDto> getProfile(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        return ResponseEntity.ok(toDto(user));
    }

    /**
     * Public profile lookup by user id — used by organizations to display the
     * applicant's real name on received applications (applications only carry the id).
     */
    @GetMapping("/by-id/{id}")
    public ResponseEntity<VolunteerProfileDto> getProfileById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .filter(u -> u.getRole() == Role.VOLUNTEER)
                .orElseThrow(() -> new UserNotFoundException("Volunteer not found: " + id));
        return ResponseEntity.ok(toDto(user));
    }

    /**
     * US-S3-07 — Authenticated volunteer fetches their own profile.
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<VolunteerProfileDto> getMyProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(toDto(user));
    }

    /**
     * T-021 — Authenticated profile update.
     * The authenticated user can only update their own profile.
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public ResponseEntity<VolunteerProfileDto> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null)  user.setLastName(request.getLastName());
        if (request.getBio() != null)       user.setBio(request.getBio());
        if (request.getPhone() != null)     user.setPhone(request.getPhone());
        if (request.getCountry() != null)   user.setCountry(request.getCountry());
        if (request.getCity() != null)      user.setCity(request.getCity());
        if (request.getAddress() != null)   user.setAddress(request.getAddress());
        if (request.getProfileImage() != null) user.setProfileImage(request.getProfileImage());
        if (request.getPreferredCategories() != null) {
            user.getPreferredCategories().clear();
            user.getPreferredCategories().addAll(request.getPreferredCategories());
        }

        User saved = userRepository.save(user);
        return ResponseEntity.ok(toDto(saved));
    }

    private VolunteerProfileDto toDto(User user) {
        return VolunteerProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .bio(user.getBio())
                .country(user.getCountry())
                .city(user.getCity())
                .profileImage(user.getProfileImage())
                .preferredCategories(user.getPreferredCategories())
                .build();
    }
}
