package payments.duo.model.request.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

import static payments.duo.utils.Constants.VALID_NOT_BLANK_MESSAGE;
import static payments.duo.utils.Constants.VALID_PASSWORD_LENGTH_MESSAGE;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserCommand extends UserCommand {
    @NotBlank(message = VALID_NOT_BLANK_MESSAGE)
    @Size(min = 6, message = VALID_PASSWORD_LENGTH_MESSAGE)
    private String password;

    public CreateUserCommand(Long userId, String username, String email, String firstName, String lastName,
                             LocalDate createdOn, String password) {
        super(userId, username, email, firstName, lastName, createdOn);
        this.password = password;
    }
}