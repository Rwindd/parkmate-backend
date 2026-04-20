package com.parkmate.adapter.inbound.rest.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterRequest {
    @NotBlank public String name;
    @NotBlank public String company;
    @NotBlank public String tower;
    @NotBlank public String floor;
    public String phone;
    @NotBlank public String deviceId;
}
