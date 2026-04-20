package com.parkmate.adapter.inbound.rest.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateEventRequest {
    @NotBlank public String module;
    @NotBlank public String activity;
    public String activityIcon;
    @NotBlank @Size(max=200) public String title;
    public String description;
    @NotNull public LocalDate eventDate;
    @NotNull public LocalTime eventTime;
    @NotBlank public String location;
    @Min(2) @Max(50) public int spots;
    @NotBlank public String visibility;
}
