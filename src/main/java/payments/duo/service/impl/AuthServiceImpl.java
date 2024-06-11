package payments.duo.service.impl;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import payments.duo.model.request.auth.SignInRequest;
import payments.duo.model.request.auth.SingInResponse;
import payments.duo.model.auth.User;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.security.jwt.JwtTokenProvider;
import payments.duo.security.jwt.JwtUser;
import payments.duo.service.AuthService;
import payments.duo.service.UserService;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationProvider authenticationProvider;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    public AuthServiceImpl(AuthenticationProvider authenticationProvider, JwtTokenProvider tokenProvider, UserService userService) {
        this.authenticationProvider = authenticationProvider;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    public SingInResponse signin(SignInRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        try {
            Authentication authenticate = authenticationProvider.authenticate(usernamePasswordAuthenticationToken);
            String token = tokenProvider.createToken(authenticate);
            JwtUser details = (JwtUser) authenticate.getPrincipal();
            return new SingInResponse(token, details.getUserId(), tokenProvider.getRoleNamesFromAuthorities(authenticate));
        } catch (AuthenticationException authenticationException) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    //TODO should return dto
    public User signup(CreateUserCommand createUserCommand) {
        return userService.registration(createUserCommand);
    }
}
