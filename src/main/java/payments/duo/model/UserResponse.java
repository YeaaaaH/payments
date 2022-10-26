package payments.duo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import payments.duo.model.request.auth.UserCommand;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    UserCommand user;
}
