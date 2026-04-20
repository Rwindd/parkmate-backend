package com.parkmate.adapter.inbound.rest.dto.response;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private String senderName;
    private String senderCompany;
    private String senderInit;
    private String senderColor;
    private String text;
    private boolean systemMessage;
    private String timeStr;
    private boolean self;
}
