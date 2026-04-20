package com.parkmate.adapter.inbound.rest.dto.response;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String token;
    private UserResponse user;
    private boolean isNewUser;
}
