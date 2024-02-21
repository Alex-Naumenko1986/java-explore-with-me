package ru.practicum.ewm.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ViewStatsDto {
    String app;
    String uri;
    Integer hits;
}
