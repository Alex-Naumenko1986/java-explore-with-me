package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.event.enums.RequestUpdateStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class RequestStatusUpdateDto {
    @Size(min = 1, message = "Size of requestsIds list should be minimum 1")
    private Set<Integer> requestIds;
    @NotNull(message = "Field status should not be null")
    private RequestUpdateStatus status;
}
