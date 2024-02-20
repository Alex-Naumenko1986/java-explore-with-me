package ru.practicum.ewm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatsEntity {
    String app;
    String uri;
    Long hits;
}
