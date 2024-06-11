package payments.duo.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import payments.duo.model.request.auth.SignInRequest;
import payments.duo.model.auth.User;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.model.request.auth.UserCommand;
import payments.duo.model.response.UserDTO;
import payments.duo.security.jwt.JwtTokenProvider;
import payments.duo.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IntegrationUserControllerTest {

    private final String userEndpoint = "/api/v1/user";

    @Autowired
    UserService userService;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Container
    public static PostgreSQLContainer<?> pgContainer = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName("duo")
            .withUsername("user")
            .withPassword("pass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", pgContainer::getJdbcUrl);
        registry.add("spring.datasource.username", pgContainer::getUsername);
        registry.add("spring.datasource.password", pgContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", pgContainer::getDriverClassName);
    }

    @Test
    void updateUserTest() {
        CreateUserCommand command = createCommandBase();
        command.setUsername("username_auth");
        command.setEmail("test@user5.mail");
        User user = userService.registration(command);

        UserCommand userCommand = new UserCommand();
        userCommand.setEmail("test@userUPDATE.mail");
        userCommand.setUsername("usernameUPDATE");

        SignInRequest signInRequest = new SignInRequest(command.getUsername(), command.getPassword());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                signInRequest.getUsername(), signInRequest.getPassword(), List.of(new SimpleGrantedAuthority("CLIENT"))
        );
        String token = tokenProvider.createToken(usernamePasswordAuthenticationToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        ResponseEntity<UserDTO> response = restTemplate.exchange(
                userEndpoint + "/" + user.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(userCommand, headers),
                UserDTO.class);
        UserDTO userResponse = response.getBody();

        assertNotNull(userResponse);
        assertEquals(userCommand.getEmail(), userResponse.getEmail());
        assertEquals(userCommand.getUsername(), userResponse.getUsername());
    }

    @Test
    void getUserFromAuthTest() {
        CreateUserCommand command = createCommandBase();
        command.setUsername("username_auth");
        command.setEmail("auth@user.mail");
        User user = userService.registration(command);

        SignInRequest signInRequest = new SignInRequest(command.getUsername(), command.getPassword());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                signInRequest.getUsername(), signInRequest.getPassword(), List.of(new SimpleGrantedAuthority("CLIENT"))
        );
        String token = tokenProvider.createToken(usernamePasswordAuthenticationToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        ResponseEntity<UserDTO> response = restTemplate.exchange(
                userEndpoint,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDTO.class);
        UserDTO userResponse = response.getBody();

        assertNotNull(userResponse);
        assertEquals(user.getEmail(), userResponse.getEmail());
        assertEquals(user.getUsername(), userResponse.getUsername());
    }

    private CreateUserCommand createCommandBase() {
        CreateUserCommand command = new CreateUserCommand();
        command.setPassword("password");
        command.setFirstName("F_name");
        command.setLastName("L_name");
        return command;
    }
}