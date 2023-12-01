package payments.duo.service;

import payments.duo.model.auth.User;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.model.request.auth.UserCommand;

public interface UserService {
    User registration(CreateUserCommand createUserCommand);
    User updateUser(UserCommand userCommand, Long id);
    UserCommand getUserFromAuth();
    User findUserByUsername(String username);
    User findUserById(Long id);
    boolean isUserExistsByUsername(String username);
    boolean isUserExistsByEmail(String email);
}