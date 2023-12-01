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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import payments.duo.model.UserResponse;
import payments.duo.model.auth.User;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.model.request.auth.UserCommand;
import payments.duo.security.jwt.JwtTokenProvider;
import payments.duo.service.UserService;
import payments.duo.utils.UserFactory;

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

    //TODO find how to launch only 1 container for tests
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
    public void updateUserTest() {
        CreateUserCommand command = createCommandBase();
        command.setUsername("username5");
        command.setEmail("test@user5.mail");
        User user = userService.registration(command);

        UserCommand userCommand = new UserCommand();
        userCommand.setEmail("test@userUPDATE.mail");
        userCommand.setUsername("usernameUPDATE");

        String token = tokenProvider.createToken(UserFactory.toJwtUser(user));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                userEndpoint + "/" + user.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(userCommand, headers),
                UserResponse.class);
        UserResponse userResponse = response.getBody();

        assertNotNull(userResponse.getUser());
        assertEquals(userCommand.getEmail(), userResponse.getUser().getEmail());
        assertEquals(userCommand.getUsername(), userResponse.getUser().getUsername());
    }

    @Test
    public void getUserFromAuthTest() {
        CreateUserCommand command = createCommandBase();
        command.setUsername("username_auth");
        command.setEmail("auth@user.mail");
        User user = userService.registration(command);

        String token = tokenProvider.createToken(UserFactory.toJwtUser(user));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                userEndpoint,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserResponse.class);
        UserResponse userResponse = response.getBody();

        assertNotNull(userResponse.getUser());
        assertEquals(user.getEmail(), userResponse.getUser().getEmail());
        assertEquals(user.getUsername(), userResponse.getUser().getUsername());
    }

    private CreateUserCommand createCommandBase() {
        CreateUserCommand command = new CreateUserCommand();
        command.setPassword("password");
        command.setFirstName("F_name");
        command.setLastName("L_name");
        return command;
    }
}