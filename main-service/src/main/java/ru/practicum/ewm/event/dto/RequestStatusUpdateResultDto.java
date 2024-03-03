package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
@ToString
public class RequestStatusUpdateResultDto {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;

}
