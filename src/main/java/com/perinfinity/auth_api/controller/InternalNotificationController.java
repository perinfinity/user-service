package com.perinfinity.auth_api.controller;

import com.perinfinity.auth_api.dtos.MessageReceivedRequest;
import com.perinfinity.auth_api.entities.Role;
import com.perinfinity.auth_api.entities.User;
import com.perinfinity.auth_api.repository.UserRepository;
import com.perinfinity.auth_api.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Endpoints internes service-à-service, protégés par clé partagée
 * (header X-Internal-Api-Key == propriété internal.api.key).
 * Une clé serveur vide désactive l'endpoint (401 systématique).
 */
@RestController
@RequestMapping("/api/internal/notifications")
public class InternalNotificationController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final String internalApiKey;
    private final String appBaseUrl;

    public InternalNotificationController(UserRepository userRepository,
                                          EmailService emailService,
                                          @Value("${internal.api.key}") String internalApiKey,
                                          @Value("${app.base-url}") String appBaseUrl) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.internalApiKey = internalApiKey;
        this.appBaseUrl = appBaseUrl;
    }

    @PostMapping("/message-received")
    public ResponseEntity<Void> messageReceived(
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey,
            @RequestBody MessageReceivedRequest request) {
        if (internalApiKey == null || internalApiKey.isBlank() || !internalApiKey.equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid internal API key");
        }
        User recipient = findUser(request.getRecipientUserId());
        User sender = findUser(request.getSenderUserId());

        String threadUrl = appBaseUrl + "/messages/" + request.getCandidatureId();
        emailService.sendNewMessageNotification(recipient.getEmail(), displayName(sender),
                request.getOpportunityTitle(), request.getPreview(), threadUrl);
        return ResponseEntity.ok().build();
    }

    private User findUser(String userId) {
        try {
            return userRepository.findById(Long.parseLong(userId))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid user id: " + userId);
        }
    }

    private String displayName(User user) {
        if (user.getRole() == Role.ORGANIZATION && user.getOrgName() != null && !user.getOrgName().isBlank()) {
            return user.getOrgName();
        }
        String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " "
                + (user.getLastName() != null ? user.getLastName() : "")).trim();
        return fullName.isBlank() ? user.getUsername() : fullName;
    }
}
