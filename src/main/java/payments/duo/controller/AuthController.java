package payments.duo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.model.request.auth.UserCommand;
import payments.duo.service.UserService;
import payments.duo.utils.UserFactory;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserCommand> registration(@RequestBody CreateUserCommand createUserCommand) {
        UserCommand userCommand = UserFactory.toUserCommand(userService.registration(createUserCommand));
        return ResponseEntity.ok(userCommand);
    }
}
