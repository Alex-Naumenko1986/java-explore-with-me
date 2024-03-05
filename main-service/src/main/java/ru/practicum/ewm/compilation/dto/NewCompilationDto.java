package ru.practicum.ewm.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class NewCompilationDto {
    private Set<Integer> events;
    private Boolean pinned;
    @NotBlank(message = "Field title should not be blank")
    @Size(min = 1, message = "Field title can't be shorter than 1 symbol")
    @Size(max = 50, message = "Field title can't be longer than 50 symbols")
    private String title;
}
