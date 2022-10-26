package payments.duo.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payments.duo.model.UserResponse;
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
    public UserResponse getUserFromAuth() {
        UserCommand userCommand = userService.getUserFromAuth();
        return new UserResponse(userCommand);
    }

    @PutMapping("{id}")
    public UserResponse updateUser(@RequestBody UserCommand userCommand, @PathVariable Long id) {
        return new UserResponse(userService.updateUser(userCommand, id));
    }
}
