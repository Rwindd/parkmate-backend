package com.parkmate.adapter.inbound.rest.dto.response;
import lombok.*;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AnonPostResponse {
    private Long id;
    private String text;
    private int relateCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private String timeAgo;
}
