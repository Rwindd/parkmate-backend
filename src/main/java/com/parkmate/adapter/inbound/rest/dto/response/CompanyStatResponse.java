package com.parkmate.adapter.inbound.rest.dto.response;
import lombok.*;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CompanyStatResponse {
    private String company;
    private long memberCount;
    private long percentage;
    private List<String> towers;
}
