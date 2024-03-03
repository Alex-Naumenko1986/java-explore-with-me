package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.event.enums.EventStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
@ToString
public class AdminSearchRequestDto {
    private Integer[] users;
    private EventStatus[] states;
    private Integer[] categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Integer from;
    private Integer size;
}
