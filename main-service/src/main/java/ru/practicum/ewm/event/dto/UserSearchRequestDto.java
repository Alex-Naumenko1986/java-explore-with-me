package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.event.enums.EventSort;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
@ToString
public class UserSearchRequestDto {
    private String text;
    private Integer[] categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private EventSort sort;
    private Integer from;
    private Integer size;
}
