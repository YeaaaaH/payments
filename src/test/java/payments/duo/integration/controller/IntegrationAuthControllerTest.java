package payments.duo.integration.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import payments.duo.integration.AbstractIntegrationTest;
import payments.duo.model.auth.User;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.model.request.auth.SignInRequest;
import payments.duo.model.request.auth.SingInResponse;
import payments.duo.model.response.ExceptionResponse;
import payments.duo.service.UserService;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static payments.duo.security.jwt.JwtTokenProvider.USER_ID_CLAIM;
import static payments.duo.utils.Constants.TOKEN_DECLARATION_IS_WRONG;

class IntegrationAuthControllerTest extends AbstractIntegrationTest {

    private static final String SIGNIN_ENDPOINT = "/api/auth/signin";

    @Autowired
    UserService userService;

    @Value("${jwt.token.secret}")
    String secret;

    @Test
    void signinReturnsTokenWithUserIdClaim() {
        User user = registerUser("signin_user");

        ResponseEntity<SingInResponse> response = restTemplate.postForEntity(SIGNIN_ENDPOINT,
                new SignInRequest("signin_user", "password"), SingInResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody().getUserId());
        DecodedJWT token = JWT.decode(response.getBody().getAccessToken());
        assertEquals("signin_user", token.getSubject());
        assertEquals(user.getId(), token.getClaim(USER_ID_CLAIM).asLong());
    }

    @Test
    void signinWithWrongPasswordIsRejected() {
        registerUser("signin_user");

        ResponseEntity<ExceptionResponse> response = restTemplate.postForEntity(SIGNIN_ENDPOINT,
                new SignInRequest("signin_user", "wrong-password"), ExceptionResponse.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody().getExceptions().get(0).getMessage());
    }

    @Test
    void tokenWithoutUserIdClaimIsRejected() {
        String token = JWT.create()
                .withSubject("no_id_user")
                .withClaim("roles", List.of("CLIENT"))
                .withExpiresAt(new Date(System.currentTimeMillis() + 60_000))
                .sign(Algorithm.HMAC256(secret.getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<ExceptionResponse> response = restTemplate.exchange("/api/v1/category", HttpMethod.GET,
                new HttpEntity<>(headers), ExceptionResponse.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(TOKEN_DECLARATION_IS_WRONG, response.getBody().getExceptions().get(0).getMessage());
    }

    private User registerUser(String username) {
        CreateUserCommand command = new CreateUserCommand();
        command.setUsername(username);
        command.setEmail(username + "@user.mail");
        command.setPassword("password");
        return userService.registration(command);
    }
}
