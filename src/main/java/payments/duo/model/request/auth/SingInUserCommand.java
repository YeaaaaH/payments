package payments.duo.model.request.auth;

import lombok.Data;

@Data
public class SingInUserCommand {
    private String username;
    private String password;
}