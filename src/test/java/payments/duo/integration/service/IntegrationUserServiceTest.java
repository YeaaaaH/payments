package payments.duo.integration.service;

import payments.duo.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import payments.duo.model.auth.User;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.service.impl.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegrationUserServiceTest extends AbstractIntegrationTest {

    @Autowired
    UserServiceImpl userService;

    @Test
    void findUserByIdTest() {
        CreateUserCommand command = createCommandBase();
        command.setUsername("username1");
        command.setEmail("test@user1.mail");

        User user = userService.registration(command);
        Long id = user.getId();

        assertNotEquals(null, user);
        assertNotNull(userService.findUserById(id));
        assertEquals(id, userService.findUserById(id).getId());
    }

    @Test
    void isUserExistsByUsernameTest() {
        CreateUserCommand command = createCommandBase();
        command.setUsername("username2");
        command.setEmail("test@user2.mail");

        User user = userService.registration(command);

        assertNotEquals(null, user);
        assertTrue(userService.isUserExistsByUsername(user.getUsername()));
    }

    @Test
    void isUserExistsByEmailTest() {
        CreateUserCommand command = createCommandBase();
        command.setUsername("username3");
        command.setEmail("test@user3.mail");

        User user = userService.registration(command);

        assertNotEquals(null, user);
        assertTrue(userService.isUserExistsByEmail(user.getEmail()));
    }

    @Test
    void findUserByUsernameTest() {
        CreateUserCommand command = createCommandBase();
        command.setUsername("username4");
        command.setEmail("test@user4.mail");

        User user = userService.registration(command);
        String username = user.getUsername();

        assertNotEquals(null, user);
        assertNotNull(userService.findUserByUsername(username));
        assertEquals(username, userService.findUserByUsername(username).getUsername());
    }

    private CreateUserCommand createCommandBase() {
        CreateUserCommand command = new CreateUserCommand();
        command.setPassword("password");
        command.setFirstName("F_name");
        command.setLastName("L_name");
        return command;
    }
}