package payments.duo.model.request.auth;

import lombok.Getter;

import java.sql.Date;

@Getter
public class CreateUserCommand extends UserCommand {
    private String password;

    public CreateUserCommand(String username, String email, String firstName, String lastName,
                             Date createdOn, Date updatedOn, String password) {
        super(username, email, firstName, lastName, createdOn, updatedOn);
        this.password = password;
    }
}