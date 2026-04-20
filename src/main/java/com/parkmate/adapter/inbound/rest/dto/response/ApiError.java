package com.parkmate.adapter.inbound.rest.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;
@Data @Builder @JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> fieldErrors;
    public static ApiError of(int status, String error, String message, String path) {
        return ApiError.builder().status(status).error(error).message(message)
            .path(path).timestamp(LocalDateTime.now()).build();
    }
}
