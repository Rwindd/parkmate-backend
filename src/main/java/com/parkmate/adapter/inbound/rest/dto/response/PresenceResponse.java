package com.parkmate.adapter.inbound.rest.dto.response;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PresenceResponse {
    private Long userId;
    private String name;
    private String company;
    private String tower;
    private String floor;
    private String init;
    private String color;
}
