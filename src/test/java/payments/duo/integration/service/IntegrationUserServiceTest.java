package payments.duo.integration.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import payments.duo.model.auth.User;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IntegrationUserServiceTest {

    @Autowired
    UserService userService;

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
    public void findUserByIdTest() {
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
    public void isUserExistsByUsernameTest() {
        CreateUserCommand command = createCommandBase();
        command.setUsername("username2");
        command.setEmail("test@user2.mail");

        User user = userService.registration(command);

        assertNotEquals(null, user);
        assertTrue(userService.isUserExistsByUsername(user.getUsername()));
    }

    @Test
    public void isUserExistsByEmailTest() {
        CreateUserCommand command = createCommandBase();
        command.setUsername("username3");
        command.setEmail("test@user3.mail");

        User user = userService.registration(command);

        assertNotEquals(null, user);
        assertTrue(userService.isUserExistsByEmail(user.getEmail()));
    }

    @Test
    public void findUserByUsernameTest() {
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