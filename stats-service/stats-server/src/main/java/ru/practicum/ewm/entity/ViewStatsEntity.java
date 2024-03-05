package ru.practicum.ewm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ViewStatsEntity {
    String app;
    String uri;
    Long hits;
}
