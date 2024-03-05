package ru.practicum.ewm.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class CategoryDto {
    private Integer id;
    @NotBlank(message = "Field name should not be blank")
    @Size(max = 50, message = "Field name length can't be more than 50 symbols")
    private String name;
}
