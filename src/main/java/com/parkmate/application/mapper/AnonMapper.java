package com.parkmate.application.mapper;

import com.parkmate.adapter.inbound.rest.dto.response.AnonPostResponse;
import com.parkmate.infrastructure.persistence.entity.AnonPostEntity;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class AnonMapper {
    public AnonPostResponse toResponse(AnonPostEntity p) {
        return AnonPostResponse.builder()
            .id(p.getId()).text(p.getText())
            .relateCount(p.getRelateCount()).commentCount(p.getCommentCount())
            .createdAt(p.getCreatedAt()).timeAgo(timeAgo(p.getCreatedAt()))
            .build();
    }
    private String timeAgo(LocalDateTime dt) {
        long mins = ChronoUnit.MINUTES.between(dt, LocalDateTime.now());
        if (mins < 1) return "just now";
        if (mins < 60) return mins + " min ago";
        long hrs = ChronoUnit.HOURS.between(dt, LocalDateTime.now());
        if (hrs < 24) return hrs + " hr ago";
        return ChronoUnit.DAYS.between(dt, LocalDateTime.now()) + " days ago";
    }
}
