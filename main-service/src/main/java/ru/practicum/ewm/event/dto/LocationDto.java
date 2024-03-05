package ru.practicum.ewm.event.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class LocationDto {
    @NotNull(message = "Field lat can't be null")
    private Float lat;
    @NotNull(message = "Field lon can't be null")
    private Float lon;
}
