package payments.duo.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payments.duo.model.auth.User;
import payments.duo.model.request.auth.UserCommand;
import payments.duo.model.response.UserDTO;
import payments.duo.service.UserService;
import payments.duo.utils.UserFactory;

@RestController
@RequestMapping("api/v1/user")
@Api(description="Operations related to users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserDTO getUserFromAuth() {
        User user = userService.getUserFromAuth();
        return UserFactory.toUserDTO(user);
    }

    @GetMapping("{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        return UserFactory.toUserDTO(user);
    }

    @PutMapping("{id}")
    public UserDTO updateUser(@RequestBody UserCommand userCommand, @PathVariable Long id) {
        User user = userService.updateUser(userCommand, id);
        return UserFactory.toUserDTO(user);
    }
}
