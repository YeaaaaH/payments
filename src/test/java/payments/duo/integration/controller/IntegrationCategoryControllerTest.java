package payments.duo.integration.controller;

import payments.duo.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import payments.duo.model.Category;
import payments.duo.model.auth.User;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.model.response.ExceptionResponse;
import payments.duo.model.response.FindAllCategoriesResponse;
import payments.duo.service.CategoryService;
import payments.duo.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static payments.duo.utils.Constants.TOKEN_NOT_FOUND;

class IntegrationCategoryControllerTest extends AbstractIntegrationTest {

    private final String categoryEndpoint = "/api/v1/category";

    @Autowired
    UserService userService;

    @Autowired
    CategoryService categoryService;

    @Test
    void findCategoryByIdTest() {
        CreateUserCommand command = createCommandBase();
        command.setUsername("jwt_username");
        command.setEmail("jwt@user.mail");
        User user = userService.registration(command);
        HttpHeaders headers = authHeaders(user);
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
        User user = userService.registration(command);
        HttpHeaders headers = authHeaders(user);
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