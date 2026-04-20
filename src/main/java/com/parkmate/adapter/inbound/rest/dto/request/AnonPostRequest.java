package com.parkmate.adapter.inbound.rest.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class AnonPostRequest { @NotBlank @Size(max=1000) private String text; }
