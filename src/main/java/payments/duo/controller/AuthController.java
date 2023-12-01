package payments.duo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payments.duo.model.auth.User;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.model.request.auth.UserCommand;
import payments.duo.service.impl.UserServiceImpl;
import payments.duo.utils.UserFactory;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "api/auth")
public class AuthController {

    private final UserServiceImpl userService;

    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserCommand> registration(@Valid @RequestBody CreateUserCommand createUserCommand) {
        User user = userService.registration(createUserCommand);
        UserCommand userCommand = UserFactory.toUserCommand(user);
        return ResponseEntity.ok(userCommand);
    }
}
