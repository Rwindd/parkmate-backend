package com.parkmate.adapter.inbound.rest.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class SendChatRequest { @NotBlank @Size(max=500) private String text; }
