package payments.duo.model.request.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import payments.duo.utils.validation.email.UniqueEmail;
import payments.duo.utils.validation.username.UniqueUsername;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

import static payments.duo.utils.Constants.VALID_MAIL_FORMAT_MESSAGE;
import static payments.duo.utils.Constants.VALID_NOT_BLANK_MESSAGE;
import static payments.duo.utils.Constants.VALID_USERNAME_LENGTH_MESSAGE;
import static payments.duo.utils.Constants.VALID_USERNAME_PATTERN_MESSAGE;
import static payments.duo.utils.Constants.VALID_USERNAME_REGEX_PATTERN;

@Getter
@Setter
@NoArgsConstructor
public class UserCommand {
    Long userId;
    @UniqueUsername
    @NotBlank(message = VALID_NOT_BLANK_MESSAGE)
    @Size(min = 4, max = 16, message = VALID_USERNAME_LENGTH_MESSAGE)
    @Pattern(regexp = VALID_USERNAME_REGEX_PATTERN, message = VALID_USERNAME_PATTERN_MESSAGE)
    private String username;
    @UniqueEmail
    @NotBlank(message = VALID_NOT_BLANK_MESSAGE)
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = VALID_MAIL_FORMAT_MESSAGE)
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate createdOn;

    public UserCommand(Long userId, String username, String email, String firstName, String lastName, LocalDate createdOn) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdOn = createdOn;
    }
}
