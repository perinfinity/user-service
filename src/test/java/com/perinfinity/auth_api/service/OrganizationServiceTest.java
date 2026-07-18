package com.perinfinity.auth_api.service;

import com.perinfinity.auth_api.dtos.OrgProfileDto;
import com.perinfinity.auth_api.dtos.UpdateOrgProfileRequest;
import com.perinfinity.auth_api.entities.Role;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.exceptions.OrganizationNotFoundException;
import com.perinfinity.auth_api.exceptions.UserNotFoundException;
import com.perinfinity.auth_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrganizationService organizationService;

    private User sampleOrg;

    @BeforeEach
    void setUp() {
        sampleOrg = User.builder()
                .id(10L)
                .email("org@example.com")
                .username("myorg")
                .password("hashed")
                .role(Role.ORGANIZATION)
                .orgName("My Org")
                .missionStatement("Help everyone")
                .about("We do good")
                .city("Paris")
                .country("FR")
                .verifiedBadge(false)
                .completenessScore(0)
                .build();
    }

    // -------------------------------------------------------------------------
    // getOrgById
    // -------------------------------------------------------------------------

    @Test
    void getOrgById_returns_dto_when_org_exists() {
        given(userRepository.findById(10L)).willReturn(Optional.of(sampleOrg));

        OrgProfileDto dto = organizationService.getOrgById(10L);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getOrgName()).isEqualTo("My Org");
        assertThat(dto.getMissionStatement()).isEqualTo("Help everyone");
        assertThat(dto.getAbout()).isEqualTo("We do good");
        assertThat(dto.getCity()).isEqualTo("Paris");
        assertThat(dto.getCountry()).isEqualTo("FR");
    }

    @Test
    void getOrgById_computes_completenessScore_atReadTime() {
        // Compte créé à l'inscription : score stocké à 0, mais tous les champs remplis.
        sampleOrg.setCompletenessScore(0);
        given(userRepository.findById(10L)).willReturn(Optional.of(sampleOrg));

        OrgProfileDto dto = organizationService.getOrgById(10L);

        assertThat(dto.getCompletenessScore()).isEqualTo(100);
    }

    @Test
    void getOrgById_throws_404_when_user_not_found() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> organizationService.getOrgById(99L))
                .isInstanceOf(OrganizationNotFoundException.class);
    }

    @Test
    void getOrgById_throws_404_when_user_is_not_organization() {
        User volunteer = User.builder()
                .id(5L)
                .email("vol@example.com")
                .username("volunteer1")
                .password("hashed")
                .role(Role.VOLUNTEER)
                .build();
        given(userRepository.findById(5L)).willReturn(Optional.of(volunteer));

        assertThatThrownBy(() -> organizationService.getOrgById(5L))
                .isInstanceOf(OrganizationNotFoundException.class);
    }

    // -------------------------------------------------------------------------
    // updateMyProfile
    // -------------------------------------------------------------------------

    @Test
    void updateMyProfile_returns_updated_dto() {
        given(userRepository.findByEmail("org@example.com")).willReturn(Optional.of(sampleOrg));
        given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        UpdateOrgProfileRequest request = new UpdateOrgProfileRequest();
        request.setOrgName("Updated Org");
        request.setCity("Lyon");

        OrgProfileDto dto = organizationService.updateMyProfile("org@example.com", request);

        assertThat(dto.getOrgName()).isEqualTo("Updated Org");
        assertThat(dto.getCity()).isEqualTo("Lyon");
        assertThat(dto.getMissionStatement()).isEqualTo("Help everyone");
    }

    @Test
    void updateMyProfile_updates_profileImage() {
        given(userRepository.findByEmail("org@example.com")).willReturn(Optional.of(sampleOrg));
        given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        UpdateOrgProfileRequest request = new UpdateOrgProfileRequest();
        request.setProfileImage("http://localhost:8089/api/v1/images/abc123");

        OrgProfileDto dto = organizationService.updateMyProfile("org@example.com", request);

        assertThat(dto.getProfileImage()).isEqualTo("http://localhost:8089/api/v1/images/abc123");
    }

    @Test
    void getOrgById_exposes_profileImage() {
        sampleOrg.setProfileImage("http://localhost:8089/api/v1/images/img42");
        given(userRepository.findById(10L)).willReturn(Optional.of(sampleOrg));

        OrgProfileDto dto = organizationService.getOrgById(10L);

        assertThat(dto.getProfileImage()).isEqualTo("http://localhost:8089/api/v1/images/img42");
    }

    @Test
    void updateMyProfile_throws_404_when_user_not_found() {
        given(userRepository.findByEmail("nobody@example.com")).willReturn(Optional.empty());

        UpdateOrgProfileRequest request = new UpdateOrgProfileRequest();

        assertThatThrownBy(() -> organizationService.updateMyProfile("nobody@example.com", request))
                .isInstanceOf(UserNotFoundException.class);
    }

    // -------------------------------------------------------------------------
    // calculateCompletenessScore
    // -------------------------------------------------------------------------

    @Test
    void completenessScore_is_100_when_all_fields_present() {
        int score = organizationService.calculateCompletenessScore(sampleOrg);
        assertThat(score).isEqualTo(100);
    }

    @Test
    void completenessScore_is_0_when_no_fields_set() {
        User empty = User.builder()
                .id(1L)
                .email("e@e.com")
                .username("e")
                .password("p")
                .role(Role.ORGANIZATION)
                .build();

        int score = organizationService.calculateCompletenessScore(empty);
        assertThat(score).isEqualTo(0);
    }

    @Test
    void completenessScore_partial_when_some_fields_set() {
        User partial = User.builder()
                .id(2L)
                .email("p@p.com")
                .username("partial")
                .password("p")
                .role(Role.ORGANIZATION)
                .orgName("OrgX")       // +20
                .city("Nice")          // +15
                .build();

        int score = organizationService.calculateCompletenessScore(partial);
        assertThat(score).isEqualTo(35);
    }

    @Test
    void completenessScore_stored_on_save() {
        given(userRepository.findByEmail("org@example.com")).willReturn(Optional.of(sampleOrg));
        given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        UpdateOrgProfileRequest request = new UpdateOrgProfileRequest();
        // all fields already populated in sampleOrg — score should be 100
        OrgProfileDto dto = organizationService.updateMyProfile("org@example.com", request);

        assertThat(dto.getCompletenessScore()).isEqualTo(100);
    }
}
