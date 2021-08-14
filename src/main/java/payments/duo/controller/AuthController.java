package payments.duo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.model.request.auth.SingInUserCommand;
import payments.duo.model.request.auth.UserCommand;
import payments.duo.security.AuthProviderImpl;
import payments.duo.security.jwt.JwtTokenProvider;
import payments.duo.service.UserService;
import payments.duo.utils.UserFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "api/auth")
public class AuthController {

    private final UserService userService;
//    private final AuthProviderImpl authProvider;
//    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService
//                          AuthProviderImpl authProvider,
//                          JwtTokenProvider jwtTokenProvider
    ) {
        this.userService = userService;
//        this.authProvider = authProvider;
//        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserCommand> registration(@RequestBody CreateUserCommand createUserCommand) {
        UserCommand userCommand = UserFactory.toUserCommand(userService.registration(createUserCommand));
        return ResponseEntity.ok(userCommand);
    }

//    @PostMapping("/signin")
//    public ResponseEntity<?> signin(@RequestBody SingInUserCommand singInUserCommand) {
//        try {
//            UsernamePasswordAuthenticationToken userPassAuth = new UsernamePasswordAuthenticationToken(
//                    singInUserCommand.getUsername(), singInUserCommand.getPassword());
//            Authentication authentication = authProvider.authenticate(userPassAuth);
//            String token = jwtTokenProvider.createToken(authentication);
//            Map<String, String> response = new HashMap<>();
//            response.put("access_token", token);
//            return ResponseEntity.ok(response);
//        } catch (AuthenticationException e) {
//            throw new BadCredentialsException("Invalid username or password");
//        }
//    }
}
