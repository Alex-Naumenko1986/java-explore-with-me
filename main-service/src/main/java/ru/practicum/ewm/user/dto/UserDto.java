package ru.practicum.ewm.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class UserDto {
    private Integer id;
    @NotBlank(message = "Field name should not be blank")
    @Size(min = 2, message = "Field name can't be shorter than 2 symbols")
    @Size(max = 250, message = "Field name can't be longer than 250 symbols")
    private String name;
    @NotBlank(message = "Field email should not be blank")
    @Email(message = "Invalid email")
    @Size(min = 6, message = "Field email can't be shorter than 6 symbols")
    @Size(max = 254, message = "Field email can't be longer than 254 symbols")
    private String email;
    private Boolean subscriptionAvailable;
    private List<UserShortDto> subscribedOn;
}
