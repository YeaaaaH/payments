package payments.duo.service;

import payments.duo.model.auth.User;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.model.request.auth.SignInRequest;
import payments.duo.model.request.auth.SingInResponse;

public interface AuthService {
    SingInResponse signin(SignInRequest request);
    User signup(CreateUserCommand createUserCommand);
}