package ru.practicum.ewm.error.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class ErrorResponse {
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;
}
