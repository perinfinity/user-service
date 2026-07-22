package com.perinfinity.auth_api.controller;

import com.perinfinity.auth_api.dtos.MessageReceivedRequest;
import com.perinfinity.auth_api.entities.Role;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.repository.UserRepository;
import com.perinfinity.auth_api.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InternalNotificationControllerTest {

    private static final String API_KEY = "test-internal-key";
    private static final String BODY = """
            {"recipientUserId":"42","senderUserId":"7","candidatureId":10,
             "opportunityTitle":"Reboisement du Wouri","preview":"Bonjour Aline"}
            """;

    @Mock UserRepository userRepository;
    @Mock EmailService emailService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InternalNotificationController controller =
                new InternalNotificationController(userRepository, emailService, API_KEY, "http://localhost:4200");
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private User user(Long id, Role role) {
        User u = new User();
        u.setId(id);
        u.setEmail("user" + id + "@test.com");
        u.setUsername("user" + id);
        u.setRole(role);
        if (role == Role.ORGANIZATION) {
            u.setOrgName("Green Cameroun");
        } else {
            u.setFirstName("Aline");
            u.setLastName("N.");
        }
        return u;
    }

    @Test
    void messageReceived_sendsEmail_withOrgNameAsSender() throws Exception {
        when(userRepository.findById(42L)).thenReturn(Optional.of(user(42L, Role.VOLUNTEER)));
        when(userRepository.findById(7L)).thenReturn(Optional.of(user(7L, Role.ORGANIZATION)));

        mockMvc.perform(post("/api/internal/notifications/message-received")
                        .header("X-Internal-Api-Key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isOk());

        verify(emailService).sendNewMessageNotification(
                eq("user42@test.com"), eq("Green Cameroun"), eq("Reboisement du Wouri"),
                eq("Bonjour Aline"), eq("http://localhost:4200/messages/10"));
    }

    @Test
    void messageReceived_401_withWrongKey() throws Exception {
        mockMvc.perform(post("/api/internal/notifications/message-received")
                        .header("X-Internal-Api-Key", "mauvaise-cle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void messageReceived_401_withMissingKey() throws Exception {
        mockMvc.perform(post("/api/internal/notifications/message-received")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void messageReceived_401_whenServerKeyBlank() throws Exception {
        InternalNotificationController noKey =
                new InternalNotificationController(userRepository, emailService, "", "http://localhost:4200");
        MockMvc mvc = MockMvcBuilders.standaloneSetup(noKey).build();

        mvc.perform(post("/api/internal/notifications/message-received")
                        .header("X-Internal-Api-Key", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void messageReceived_404_whenRecipientUnknown() throws Exception {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/internal/notifications/message-received")
                        .header("X-Internal-Api-Key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isNotFound());
    }
}
