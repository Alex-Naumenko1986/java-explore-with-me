package ru.practicum.ewm.compilation.dto;

import lombok.*;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class CompilationDto {
    private Integer id;
    private Boolean pinned;
    private String title;
    private List<EventShortDto> events;
}
