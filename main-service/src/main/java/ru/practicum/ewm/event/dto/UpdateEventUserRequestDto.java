package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.event.enums.UserStateAction;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class UpdateEventUserRequestDto extends BaseUpdateEventRequestDto {

    private UserStateAction stateAction;
}
