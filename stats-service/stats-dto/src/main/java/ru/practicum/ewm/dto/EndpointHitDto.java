package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.constant.Constants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EndpointHitDto {
    private Integer id;
    @NotBlank(message = "Field app should not be blank")
    private String app;
    @NotBlank(message = "Field uri should not be blank")
    private String uri;
    @NotBlank(message = "Field ip should not be blank")
    private String ip;
    @NotNull(message = "Field timestamp should not be null")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private LocalDateTime timestamp;
}
