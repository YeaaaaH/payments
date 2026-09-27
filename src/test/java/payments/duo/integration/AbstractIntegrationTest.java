package payments.duo.integration;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import payments.duo.model.auth.User;
import payments.duo.security.jwt.JwtTokenProvider;
import payments.duo.utils.UserFactory;

/**
 * Base class for integration tests: one Postgres container shared by all test classes
 * (started once per JVM) and a clean database after every test, so tests never depend
 * on each other's data or run order. Seed data (categories, roles) is kept.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("duo")
            .withUsername("user")
            .withPassword("pass");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    }

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE payments, user_roles, users RESTART IDENTITY CASCADE");
    }

    /** Headers with a valid Bearer token for the given (saved) user. */
    protected HttpHeaders authHeaders(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenProvider.createToken(UserFactory.toJwtUser(user)));
        return headers;
    }
}
