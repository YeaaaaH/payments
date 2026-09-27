package payments.duo.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payments.duo.exception.PaymentNotFoundException;
import payments.duo.model.Category;
import payments.duo.model.Payment;
import payments.duo.model.auth.User;
import payments.duo.model.request.CreatePaymentCommand;
import payments.duo.model.request.UpdatePaymentCommand;
import payments.duo.model.response.PaymentReportResponse;
import payments.duo.model.response.PaymentReportResponseParameters;
import payments.duo.model.response.PaymentResponse;
import payments.duo.repository.PaymentRepository;
import payments.duo.service.PaymentService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static payments.duo.utils.Constants.PAYMENT_NOT_FOUND_MESSAGE;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final UserServiceImpl userService;
    private final PaymentRepository paymentRepository;
    private final CategoryServiceImpl categoryService;
    private final ZoneId zone;

    public PaymentServiceImpl(UserServiceImpl userService, PaymentRepository paymentRepository, CategoryServiceImpl categoryService,
                              @Value("${app.timezone}") ZoneId zone) {
        this.userService = userService;
        this.paymentRepository = paymentRepository;
        this.categoryService = categoryService;
        this.zone = zone;
    }

    public PaymentResponse findPaymentById(Long userId, Long id) {
        return setPaymentResponse(findOwnPayment(userId, id));
    }

    public PaymentResponse savePayment(Long userId, CreatePaymentCommand command) {
        Payment payment = preparePaymentToSave(userId, command);
        return setPaymentResponse(paymentRepository.save(payment));
    }

    public PaymentResponse updatePayment(Long userId, UpdatePaymentCommand command, Long id) {
        Payment payment = findOwnPayment(userId, id);
        payment.setAmount(command.getAmount());
        payment.setDescription(command.getDescription());
        payment.setTitle(command.getTitle());
        if (Objects.nonNull(command.getCategoryId())) {
            Category category = categoryService.findCategoryById(command.getCategoryId());
            payment.setCategory(category);
        }
        // flush so @UpdateTimestamp is applied before the response is built
        return setPaymentResponse(paymentRepository.saveAndFlush(payment));
    }

    public void deletePaymentById(Long userId, Long id) {
        paymentRepository.delete(findOwnPayment(userId, id));
    }

    public void saveAllPayments(Long userId, List<CreatePaymentCommand> commandsList) {
        List<Payment> paymentsList = new ArrayList<>();
        commandsList.forEach(command -> paymentsList.add(preparePaymentToSave(userId, command)));
        paymentRepository.saveAll(paymentsList);
    }

    /** Another user's payment is treated as missing, so its existence is not revealed. */
    private Payment findOwnPayment(Long userId, Long id) {
        return paymentRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new PaymentNotFoundException(String.format(PAYMENT_NOT_FOUND_MESSAGE, id)));
    }

    public List<PaymentResponse> findAllByUserForYear(Long userId, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        List<Payment> payments = paymentRepository.findAllByUserForPeriod(userId, toInstant(start), toInstant(start.plusYears(1)));
        return payments.stream().map(this::setPaymentResponse).toList();
    }

    public List<PaymentResponse> findAllByUserForYearAndMonth(Long userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        List<Payment> payments = paymentRepository.findAllByUserForPeriod(userId, toInstant(start), toInstant(start.plusMonths(1)));
        return payments.stream().map(this::setPaymentResponse).toList();
    }

    public PaymentReportResponse calculateYearlyByUserAndCategory(Long userId, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        List<PaymentReportResponseParameters> reportResponses = paymentRepository.calculateByUserAndCategoryForPeriod(
                userId, toInstant(start), toInstant(start.plusYears(1)));
        return getPaymentReportResponse(reportResponses);
    }

    public PaymentReportResponse calculateMonthlyByUserAndCategory(Long userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        List<PaymentReportResponseParameters> reportResponses = paymentRepository.calculateByUserAndCategoryForPeriod(
                userId, toInstant(start), toInstant(start.plusMonths(1)));
        return getPaymentReportResponse(reportResponses);
    }

    /** Start of the given day in the app time zone. */
    private Instant toInstant(LocalDate day) {
        return day.atStartOfDay(zone).toInstant();
    }

    private PaymentResponse setPaymentResponse(Payment payment) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setId(payment.getId());
        paymentResponse.setTitle(payment.getTitle());
        paymentResponse.setDescription(payment.getDescription());
        paymentResponse.setAmount(payment.getAmount());
        paymentResponse.setCategoryName(payment.getCategory().getName());
        paymentResponse.setPaidAt(payment.getPaidAt());
        paymentResponse.setUpdatedAt(payment.getUpdatedAt());
        return paymentResponse;
    }

    private PaymentReportResponse getPaymentReportResponse(List<PaymentReportResponseParameters> reportResponses) {
        Map<String, BigDecimal> result = new HashMap<>();
        reportResponses.forEach(parameter -> result.put(parameter.getCategory(), parameter.getAmount()));
        return new PaymentReportResponse(result);
    }

    private Payment preparePaymentToSave(Long userId, CreatePaymentCommand command) {
        Payment payment = new Payment();
        Category category = categoryService.findCategoryById(command.getCategoryId());
        User user = userService.findUserById(userId);
        payment.setUser(user);
        payment.setAmount(command.getAmount());
        payment.setPaidAt(command.getPaidAt());
        payment.setCategory(category);
        payment.setTitle(command.getTitle());
        payment.setDescription(command.getDescription());
        return payment;
    }
}
