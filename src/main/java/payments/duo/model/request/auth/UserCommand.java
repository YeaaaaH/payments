package payments.duo.model.request.auth;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Date;

import static payments.duo.utils.Constants.VALID_USERNAME_LENGTH_MESSAGE;
import static payments.duo.utils.Constants.VALID_USERNAME_PATTERN_MESSAGE;
import static payments.duo.utils.Constants.VALID_USERNAME_REGEX_PATTERN;

@Getter
public class UserCommand {
    @NotBlank
    @Size(min = 4, max = 16, message = VALID_USERNAME_LENGTH_MESSAGE)
    @Pattern(regexp = VALID_USERNAME_REGEX_PATTERN, message = VALID_USERNAME_PATTERN_MESSAGE)
    private String username;
    @NotBlank
    @Email
    private String email;
    private String firstName;
    private String lastName;
    private Date createdOn;
    private Date updatedOn;

    public UserCommand(String username, String email, String firstName, String lastName, Date createdOn, Date updatedOn) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }
}
