package payments.duo.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payments.duo.model.UserFromAuthResponse;
import payments.duo.model.request.auth.UserCommand;
import payments.duo.service.UserService;

@RestController
@RequestMapping("api/v1/user")
@Api(description="Operations related to users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserFromAuthResponse getUserFromAuth() {
        UserCommand userCommand = userService.getUserFromAuth();
        return new UserFromAuthResponse(userCommand);
    }
    //TODO add user update
}
