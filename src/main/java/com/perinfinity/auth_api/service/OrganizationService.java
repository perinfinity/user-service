package com.perinfinity.auth_api.service;

import com.perinfinity.auth_api.dtos.OrgProfileDto;
import com.perinfinity.auth_api.dtos.UpdateOrgProfileRequest;
import com.perinfinity.auth_api.entities.Role;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.exceptions.OrganizationNotFoundException;
import com.perinfinity.auth_api.exceptions.UserNotFoundException;
import com.perinfinity.auth_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final UserRepository userRepository;

    /**
     * T-061 — Public org profile lookup by ID.
     * Returns 404 if the user is not found or does not have the ORGANIZATION role.
     */
    public OrgProfileDto getOrgById(Long id) {
        User user = userRepository.findById(id)
                .filter(u -> u.getRole() == Role.ORGANIZATION)
                .orElseThrow(() -> new OrganizationNotFoundException(id));
        return toDto(user);
    }

    /**
     * T-061 — Authenticated org profile update.
     * Recalculates completenessScore after updating fields.
     */
    public OrgProfileDto updateMyProfile(String email, UpdateOrgProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (request.getOrgName() != null)          user.setOrgName(request.getOrgName());
        if (request.getMissionStatement() != null)  user.setMissionStatement(request.getMissionStatement());
        if (request.getAbout() != null)             user.setAbout(request.getAbout());
        if (request.getCity() != null)              user.setCity(request.getCity());
        if (request.getCountry() != null)           user.setCountry(request.getCountry());
        if (request.getProfileImage() != null)      user.setProfileImage(request.getProfileImage());

        user.setCompletenessScore(calculateCompletenessScore(user));

        User saved = userRepository.save(user);
        return toDto(saved);
    }

    /**
     * Completeness score (out of 100):
     * - orgName:          +20
     * - missionStatement: +25
     * - about:            +25
     * - city:             +15
     * - country:          +15
     */
    int calculateCompletenessScore(User user) {
        int score = 0;
        if (isNotBlank(user.getOrgName()))          score += 20;
        if (isNotBlank(user.getMissionStatement()))  score += 25;
        if (isNotBlank(user.getAbout()))             score += 25;
        if (isNotBlank(user.getCity()))              score += 15;
        if (isNotBlank(user.getCountry()))           score += 15;
        return score;
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    private OrgProfileDto toDto(User user) {
        return OrgProfileDto.builder()
                .id(user.getId())
                .orgName(user.getOrgName())
                .missionStatement(user.getMissionStatement())
                .about(user.getAbout())
                .verifiedBadge(user.isVerifiedBadge())
                // Calculé à la lecture : la valeur stockée reste à 0 pour les comptes
                // créés à l'inscription (elle n'était recalculée qu'à la mise à jour).
                .completenessScore(calculateCompletenessScore(user))
                .city(user.getCity())
                .country(user.getCountry())
                .profileImage(user.getProfileImage())
                .build();
    }
}
