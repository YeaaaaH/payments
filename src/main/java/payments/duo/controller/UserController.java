package payments.duo.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payments.duo.model.request.auth.UserCommand;
import payments.duo.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
@Api(description="Operations related to users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Map<String, UserCommand> getUserFromAuth() {
        UserCommand userCommand = userService.getUserFromAuth();
        return Map.of("user", userCommand);
    }
}
