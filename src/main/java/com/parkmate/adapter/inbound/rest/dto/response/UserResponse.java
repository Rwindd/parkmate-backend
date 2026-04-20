package com.parkmate.adapter.inbound.rest.dto.response;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String company;
    private String tower;
    private String floor;
    private boolean atClockpoint;
    private String init;
    private String color;
}
