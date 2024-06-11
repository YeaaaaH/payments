package payments.duo.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import payments.duo.integration.configs.IntegrationTestConfig;
import payments.duo.model.Category;
import payments.duo.model.request.auth.SignInRequest;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.model.response.ExceptionResponse;
import payments.duo.model.response.FindAllCategoriesResponse;
import payments.duo.security.jwt.JwtTokenProvider;
import payments.duo.service.CategoryService;
import payments.duo.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static payments.duo.utils.Constants.TOKEN_NOT_FOUND;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = IntegrationTestConfig.class
)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IntegrationCategoryControllerTest {

    private final String categoryEndpoint = "/api/v1/category";

    @Autowired
    UserService userService;

    @Autowired
    CategoryService categoryService;

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
    void findCategoryByIdTest() {
        CreateUserCommand command = createCommandBase();
        command.setUsername("jwt_username");
        command.setEmail("jwt@user.mail");
        userService.registration(command);
        SignInRequest signInRequest = new SignInRequest(command.getUsername(), command.getPassword());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                signInRequest.getUsername(), signInRequest.getPassword(), List.of(new SimpleGrantedAuthority("CLIENT"))
        );
        String token = tokenProvider.createToken(usernamePasswordAuthenticationToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        ResponseEntity<Category> response = restTemplate.exchange(categoryEndpoint + "/1", HttpMethod.GET, new HttpEntity<>(headers), Category.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Food", response.getBody().getName());
    }

    @Test
    void findAllCategoriesTest() {
        CreateUserCommand command = createCommandBase();
        command.setUsername("jwt_username1");
        command.setEmail("jwt@user1.mail");
        userService.registration(command);
        SignInRequest signInRequest = new SignInRequest(command.getUsername(), command.getPassword());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                signInRequest.getUsername(), signInRequest.getPassword(), List.of(new SimpleGrantedAuthority("CLIENT"))
        );
        String token = tokenProvider.createToken(usernamePasswordAuthenticationToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        ResponseEntity<FindAllCategoriesResponse> response = restTemplate.exchange(categoryEndpoint, HttpMethod.GET, new HttpEntity<>(headers), FindAllCategoriesResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getCategories());
        assertEquals(14, response.getBody().getCategories().size());
    }

    @Test
    void findAllCategoriesNoTokenProvidedTest() {
        //when
        ResponseEntity<ExceptionResponse> response = restTemplate.exchange(categoryEndpoint, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), ExceptionResponse.class);

        //then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody().getExceptions());
        assertEquals(response.getBody().getExceptions().get(0).getMessage(),TOKEN_NOT_FOUND);
    }

    private CreateUserCommand createCommandBase() {
        CreateUserCommand command = new CreateUserCommand();
        command.setPassword("password");
        command.setFirstName("F_name");
        command.setLastName("L_name");
        return command;
    }
}