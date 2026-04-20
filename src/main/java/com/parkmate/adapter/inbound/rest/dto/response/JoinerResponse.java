package com.parkmate.adapter.inbound.rest.dto.response;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class JoinerResponse {
    private Long id;
    private String name;
    private String company;
    private String tower;
    private String floor;
    private String phone;
    private String init;
    private String color;
}
