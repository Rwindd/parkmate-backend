package com.parkmate.adapter.inbound.rest.dto.response;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EventResponse {
    private Long id;
    private String module;
    private String activity;
    private String activityIcon;
    private String title;
    private String description;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private String location;
    private int spots;
    private int spotsLeft;
    private boolean full;
    private String visibility;
    private UserResponse creator;
    private List<JoinerResponse> joiners;
    private boolean active;
    private boolean expired;
    private boolean isCreator;
    private boolean isJoined;
    private LocalDateTime createdAt;
}
