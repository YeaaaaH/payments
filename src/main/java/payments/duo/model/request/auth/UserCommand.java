package payments.duo.model.request.auth;

import lombok.Getter;

import java.sql.Date;

@Getter
public class UserCommand {
    private String username;
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
