package com.perinfinity.auth_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageReceivedRequest {
    private String recipientUserId;
    private String senderUserId;
    private Long candidatureId;
    private String opportunityTitle;
    private String preview;
}
