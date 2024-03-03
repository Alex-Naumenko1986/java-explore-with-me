package ru.practicum.ewm.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.constant.Constants;
import ru.practicum.ewm.request.enums.RequestStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class RequestDto {
    private Integer id;
    private Integer requester;
    private Integer event;
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private LocalDateTime created;
    private RequestStatus status;
}
