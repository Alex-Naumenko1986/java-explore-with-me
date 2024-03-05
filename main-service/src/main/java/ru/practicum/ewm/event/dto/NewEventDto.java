package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.constant.Constants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class NewEventDto {

    @NotBlank(message = "Field title should not be blank")
    @Size(min = 3, message = "Field title can't be shorter than 3 symbols")
    @Size(max = 120, message = "Field title can't be longer than 120 symbols")
    private String title;

    @NotBlank(message = "Field annotation should not be blank")
    @Size(min = 20, message = "Field annotation can't be shorter than 20 symbols")
    @Size(max = 2000, message = "Field annotation can't be longer than 2000 symbols")
    private String annotation;

    @NotBlank(message = "Field description should not be blank")
    @Size(min = 20, message = "Field description can't be shorter than 20 symbols")
    @Size(max = 7000, message = "Field description can't be longer than 7000 symbols")
    private String description;

    @NotNull(message = "Field category can't be null")
    private Integer category;

    @NotNull(message = "Location can not be null")
    private LocationDto location;

    private Boolean paid;

    private Boolean requestModeration;

    @PositiveOrZero(message = "Field participantLimit should be positive or zero")
    private Integer participantLimit;

    @NotNull(message = "Field eventDate can't be null")
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

}
