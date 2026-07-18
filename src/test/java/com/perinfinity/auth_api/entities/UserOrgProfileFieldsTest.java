package com.perinfinity.auth_api.entities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserOrgProfileFieldsTest {

    @Test
    void user_shouldHaveMissionStatementField() throws NoSuchFieldException {
        var field = User.class.getDeclaredField("missionStatement");
        assertThat(field.getType()).isEqualTo(String.class);
    }

    @Test
    void user_shouldHaveAboutField() throws NoSuchFieldException {
        var field = User.class.getDeclaredField("about");
        assertThat(field.getType()).isEqualTo(String.class);
    }

    @Test
    void user_shouldHaveVerifiedBadgeField() throws NoSuchFieldException {
        var field = User.class.getDeclaredField("verifiedBadge");
        assertThat(field.getType()).isEqualTo(boolean.class);
    }

    @Test
    void user_shouldHaveCompletenessScoreField() throws NoSuchFieldException {
        var field = User.class.getDeclaredField("completenessScore");
        assertThat(field.getType()).isEqualTo(int.class);
    }

    @Test
    void user_verifiedBadge_defaultShouldBeFalse() {
        User user = new User();
        assertThat(user.isVerifiedBadge()).isFalse();
    }

    @Test
    void user_completenessScore_defaultShouldBeZero() {
        User user = new User();
        assertThat(user.getCompletenessScore()).isEqualTo(0);
    }

    @Test
    void user_shouldSetAndGetMissionStatement() {
        User user = new User();
        user.setMissionStatement("We help communities grow.");
        assertThat(user.getMissionStatement()).isEqualTo("We help communities grow.");
    }

    @Test
    void user_shouldSetAndGetAbout() {
        User user = new User();
        user.setAbout("A non-profit focused on education.");
        assertThat(user.getAbout()).isEqualTo("A non-profit focused on education.");
    }
}
