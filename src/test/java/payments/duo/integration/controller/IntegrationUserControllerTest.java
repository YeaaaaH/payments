package payments.duo.integration.controller;

import payments.duo.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import payments.duo.model.auth.User;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.model.request.auth.UserCommand;
import payments.duo.model.response.UserDTO;
import payments.duo.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IntegrationUserControllerTest extends AbstractIntegrationTest {

    private final String userEndpoint = "/api/v1/user";

    @Autowired
    UserService userService;

    @Test
    void updateUserTest() {
        CreateUserCommand command = createCommandBase();
        command.setUsername("username_auth");
        command.setEmail("test@user5.mail");
        User user = userService.registration(command);

        UserCommand userCommand = new UserCommand();
        userCommand.setEmail("test@userUPDATE.mail");
        userCommand.setUsername("usernameUPDATE");

        HttpHeaders headers = authHeaders(user);
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

        HttpHeaders headers = authHeaders(user);
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

    @Test
    void getOwnUserByIdTest() {
        User user = registerUser("own_user");

        ResponseEntity<UserDTO> response = restTemplate.exchange(userEndpoint + "/" + user.getId(),
                HttpMethod.GET, new HttpEntity<>(authHeaders(user)), UserDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getUsername(), response.getBody().getUsername());
    }

    @Test
    void getAnotherUserByIdIsNotFound() {
        User user = registerUser("own_user");
        User other = registerUser("other_user");

        ResponseEntity<String> response = restTemplate.exchange(userEndpoint + "/" + other.getId(),
                HttpMethod.GET, new HttpEntity<>(authHeaders(user)), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateAnotherUserIsNotFoundAndLeavesItUnchanged() {
        User user = registerUser("own_user");
        User other = registerUser("other_user");
        UserCommand userCommand = new UserCommand();
        userCommand.setUsername("hijacked");

        ResponseEntity<String> response = restTemplate.exchange(userEndpoint + "/" + other.getId(),
                HttpMethod.PUT, new HttpEntity<>(userCommand, authHeaders(user)), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("other_user", userService.findUserById(other.getId()).getUsername());
    }

    private User registerUser(String username) {
        CreateUserCommand command = createCommandBase();
        command.setUsername(username);
        command.setEmail(username + "@user.mail");
        return userService.registration(command);
    }

    private CreateUserCommand createCommandBase() {
        CreateUserCommand command = new CreateUserCommand();
        command.setPassword("password");
        command.setFirstName("F_name");
        command.setLastName("L_name");
        return command;
    }
}