package payments.duo.integration.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import payments.duo.integration.AbstractIntegrationTest;
import payments.duo.model.Payment;
import payments.duo.model.auth.User;
import payments.duo.model.request.CreatePaymentCommand;
import payments.duo.model.request.CreatePaymentsListCommand;
import payments.duo.model.request.UpdatePaymentCommand;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.model.response.PaymentReportResponse;
import payments.duo.model.response.PaymentResponse;
import payments.duo.repository.CategoryRepository;
import payments.duo.repository.PaymentRepository;
import payments.duo.security.jwt.JwtTokenProvider;
import payments.duo.service.UserService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegrationPaymentControllerTest extends AbstractIntegrationTest {

    private static final String PAYMENT_ENDPOINT = "/api/v1/payment";
    private static final long FOOD = 1L;
    private static final long TAXI = 10L;

    @Autowired
    UserService userService;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

    private User user;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        user = registerUser("payer");
        headers = authHeaders(user);
    }

    @Test
    void createPaymentSavesAndReturnsIt() {
        CreatePaymentCommand command = createCommand("12.50", FOOD, Instant.parse("2026-09-01T10:00:00Z"));

        ResponseEntity<PaymentResponse> response = exchange(HttpMethod.POST, "", command, PaymentResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaymentResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Coffee", body.getTitle());
        assertEquals("note", body.getDescription());
        assertEquals(new BigDecimal("12.50"), body.getAmount());
        assertEquals("Food", body.getCategoryName());
        assertEquals(Instant.parse("2026-09-01T10:00:00Z"), body.getPaidAt());
        Payment saved = paymentRepository.findAll().get(0);
        assertEquals(user.getId(), saved.getUser().getId());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void createPaymentWithMoreThanTwoDecimalsIsRejected() {
        CreatePaymentCommand command = createCommand("12.345", FOOD, Instant.parse("2026-09-01T10:00:00Z"));

        ResponseEntity<String> response = exchange(HttpMethod.POST, "", command, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, paymentRepository.count());
    }

    @Test
    void createPaymentWithNonPositiveAmountIsRejected() {
        CreatePaymentCommand command = createCommand("0", FOOD, Instant.parse("2026-09-01T10:00:00Z"));

        ResponseEntity<String> response = exchange(HttpMethod.POST, "", command, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, paymentRepository.count());
    }

    @Test
    void getPaymentReturnsIt() {
        Long id = savePayment("25.00", TAXI, Instant.parse("2026-09-01T10:00:00Z"));

        ResponseEntity<PaymentResponse> response = exchange(HttpMethod.GET, "/" + id, null, PaymentResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new BigDecimal("25.00"), response.getBody().getAmount());
        assertEquals("Taxi", response.getBody().getCategoryName());
    }

    @Test
    void getUnknownPaymentIsNotFound() {
        ResponseEntity<String> response = exchange(HttpMethod.GET, "/999", null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updatePaymentChangesIt() {
        Long id = savePayment("25.00", FOOD, Instant.parse("2026-09-01T10:00:00Z"));

        ResponseEntity<PaymentResponse> response = exchange(HttpMethod.PUT, "/" + id,
                updateCommand("30.00", TAXI), PaymentResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new BigDecimal("30.00"), response.getBody().getAmount());
        assertEquals("Taxi", response.getBody().getCategoryName());
        assertEquals("Updated", response.getBody().getTitle());
        Payment saved = paymentRepository.findById(id).orElseThrow();
        assertEquals(new BigDecimal("30.00"), saved.getAmount());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void updatePaymentWithMoreThanTwoDecimalsIsRejected() {
        Long id = savePayment("25.00", FOOD, Instant.parse("2026-09-01T10:00:00Z"));

        ResponseEntity<String> response = exchange(HttpMethod.PUT, "/" + id, updateCommand("30.001", FOOD), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(new BigDecimal("25.00"), paymentRepository.findById(id).orElseThrow().getAmount());
    }

    @Test
    void deletePaymentRemovesIt() {
        Long id = savePayment("25.00", FOOD, Instant.parse("2026-09-01T10:00:00Z"));

        ResponseEntity<String> response = exchange(HttpMethod.DELETE, "/" + id, null, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(paymentRepository.findById(id).isEmpty());
    }

    @Test
    void saveAllCreatesEveryPayment() {
        CreatePaymentsListCommand command = new CreatePaymentsListCommand();
        command.setPaymentCommands(List.of(
                createCommand("10.00", FOOD, Instant.parse("2026-09-01T10:00:00Z")),
                createCommand("20.00", TAXI, Instant.parse("2026-09-02T10:00:00Z"))));

        ResponseEntity<String> response = exchange(HttpMethod.POST, "/saveAll", command, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, paymentRepository.count());
    }

    @Test
    void monthlyListUsesAppTimeZoneBoundaries() {
        // 2026-09-30T21:30Z is 2026-10-01 00:30 in Kyiv (app.timezone)
        savePayment("10.00", FOOD, Instant.parse("2026-09-30T21:30:00Z"));
        savePayment("20.00", FOOD, Instant.parse("2026-09-30T20:30:00Z"));

        List<PaymentResponse> september = getList("/list/monthly?userId=" + user.getId() + "&year=2026&month=9");
        List<PaymentResponse> october = getList("/list/monthly?userId=" + user.getId() + "&year=2026&month=10");

        assertEquals(1, september.size());
        assertEquals(new BigDecimal("20.00"), september.get(0).getAmount());
        assertEquals(1, october.size());
        assertEquals(new BigDecimal("10.00"), october.get(0).getAmount());
    }

    @Test
    void yearlyListUsesAppTimeZoneBoundaries() {
        // 2025-12-31T22:30Z is 2026-01-01 00:30 in Kyiv, 2026-12-31T22:30Z is already 2027
        savePayment("10.00", FOOD, Instant.parse("2025-12-31T22:30:00Z"));
        savePayment("20.00", FOOD, Instant.parse("2026-06-15T10:00:00Z"));
        savePayment("30.00", FOOD, Instant.parse("2026-12-31T22:30:00Z"));

        List<PaymentResponse> year2026 = getList("/list/yearly?userId=" + user.getId() + "&year=2026");

        assertEquals(List.of(new BigDecimal("10.00"), new BigDecimal("20.00")),
                year2026.stream().map(PaymentResponse::getAmount).sorted().toList());
    }

    @Test
    void listsContainOnlyPaymentsOfRequestedUser() {
        savePayment("10.00", FOOD, Instant.parse("2026-09-10T10:00:00Z"));
        User other = registerUser("other");
        savePayment(other, "99.00", FOOD, Instant.parse("2026-09-10T10:00:00Z"));

        List<PaymentResponse> payments = getList("/list/monthly?userId=" + user.getId() + "&year=2026&month=9");

        assertEquals(1, payments.size());
        assertEquals(new BigDecimal("10.00"), payments.get(0).getAmount());
    }

    @Test
    void monthlyReportSumsPerCategory() {
        savePayment("10.25", FOOD, Instant.parse("2026-09-01T10:00:00Z"));
        savePayment("20.25", FOOD, Instant.parse("2026-09-20T10:00:00Z"));
        savePayment("7.00", TAXI, Instant.parse("2026-09-05T10:00:00Z"));
        savePayment("999.00", FOOD, Instant.parse("2026-10-05T10:00:00Z"));

        Map<String, BigDecimal> report = getReport("/report/monthly?userId=" + user.getId() + "&year=2026&month=9");

        assertEquals(2, report.size());
        assertEquals(0, new BigDecimal("30.50").compareTo(report.get("Food")));
        assertEquals(0, new BigDecimal("7.00").compareTo(report.get("Taxi")));
    }

    @Test
    void yearlyReportSumsPerCategory() {
        savePayment("10.00", FOOD, Instant.parse("2026-01-10T10:00:00Z"));
        savePayment("15.00", FOOD, Instant.parse("2026-11-10T10:00:00Z"));
        savePayment("999.00", FOOD, Instant.parse("2025-06-10T10:00:00Z"));

        Map<String, BigDecimal> report = getReport("/report/yearly?userId=" + user.getId() + "&year=2026");

        assertEquals(1, report.size());
        assertEquals(0, new BigDecimal("25.00").compareTo(report.get("Food")));
    }

    private User registerUser(String username) {
        CreateUserCommand command = new CreateUserCommand();
        command.setUsername(username);
        command.setEmail(username + "@user.mail");
        command.setPassword("password");
        return userService.registration(command);
    }

    private HttpHeaders authHeaders(User user) {
        String token = tokenProvider.createToken(new UsernamePasswordAuthenticationToken(
                user.getUsername(), null, List.of(new SimpleGrantedAuthority("CLIENT"))));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    private CreatePaymentCommand createCommand(String amount, long categoryId, Instant paidAt) {
        CreatePaymentCommand command = new CreatePaymentCommand();
        command.setAmount(new BigDecimal(amount));
        command.setCategoryId(categoryId);
        command.setTitle("Coffee");
        command.setDescription("note");
        command.setUserId(user.getId());
        command.setPaidAt(paidAt);
        return command;
    }

    private UpdatePaymentCommand updateCommand(String amount, long categoryId) {
        UpdatePaymentCommand command = new UpdatePaymentCommand();
        command.setAmount(new BigDecimal(amount));
        command.setCategoryId(categoryId);
        command.setTitle("Updated");
        command.setUserId(user.getId());
        return command;
    }

    private Long savePayment(String amount, long categoryId, Instant paidAt) {
        return savePayment(user, amount, categoryId, paidAt);
    }

    // PaymentResponse has no id yet, so fixtures are saved through the repository
    private Long savePayment(User owner, String amount, long categoryId, Instant paidAt) {
        Payment payment = new Payment();
        payment.setUser(owner);
        payment.setCategory(categoryRepository.findById(categoryId).orElseThrow());
        payment.setTitle("Fixture");
        payment.setAmount(new BigDecimal(amount));
        payment.setPaidAt(paidAt);
        return paymentRepository.save(payment).getId();
    }

    private <T> ResponseEntity<T> exchange(HttpMethod method, String path, Object body, Class<T> responseType) {
        return restTemplate.exchange(PAYMENT_ENDPOINT + path, method, new HttpEntity<>(body, headers), responseType);
    }

    private List<PaymentResponse> getList(String path) {
        ResponseEntity<List<PaymentResponse>> response = restTemplate.exchange(PAYMENT_ENDPOINT + path,
                HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    private Map<String, BigDecimal> getReport(String path) {
        ResponseEntity<PaymentReportResponse> response = exchange(HttpMethod.GET, path, null, PaymentReportResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody().getReport();
    }
}
