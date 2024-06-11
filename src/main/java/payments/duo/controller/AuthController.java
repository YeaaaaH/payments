package payments.duo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payments.duo.model.request.auth.SignInRequest;
import payments.duo.model.request.auth.SingInResponse;
import payments.duo.model.auth.User;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.model.response.UserDTO;
import payments.duo.service.impl.AuthServiceImpl;
import payments.duo.utils.UserFactory;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "api/auth")
public class AuthController {

    private final AuthServiceImpl authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/signin", produces = "application/json", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<SingInResponse> signin(@RequestBody SignInRequest requestDTO) {
        return new ResponseEntity<>(authService.signin(requestDTO), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> registration(@Valid @RequestBody CreateUserCommand createUserCommand) {
        User user = authService.signup(createUserCommand);
        return ResponseEntity.ok(UserFactory.toUserDTO(user));
    }
}
