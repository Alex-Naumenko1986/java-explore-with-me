package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.event.enums.AdminStateAction;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class UpdateEventAdminRequestDto extends BaseUpdateEventRequestDto {

   private AdminStateAction stateAction;
}
