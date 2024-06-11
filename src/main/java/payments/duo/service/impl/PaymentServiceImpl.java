package payments.duo.service.impl;

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

    public PaymentServiceImpl(UserServiceImpl userService, PaymentRepository paymentRepository, CategoryServiceImpl categoryService) {
        this.userService = userService;
        this.paymentRepository = paymentRepository;
        this.categoryService = categoryService;
    }

    public PaymentResponse findPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(String.format(PAYMENT_NOT_FOUND_MESSAGE, id)));
        return setPaymentResponse(payment);
    }

    public PaymentResponse savePayment(CreatePaymentCommand command) {
        Payment payment = preparePaymentToSave(command);
        return setPaymentResponse(paymentRepository.save(payment));
    }

    public PaymentResponse updatePayment(UpdatePaymentCommand command, Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(String.format(PAYMENT_NOT_FOUND_MESSAGE, command.getId())));
        payment.setAmount(command.getAmount());
        payment.setDescription(command.getDescription());
        payment.setTitle(command.getTitle());
        if (Objects.nonNull(command.getCategoryId())) {
            Category category = categoryService.findCategoryById(command.getCategoryId());
            payment.setCategory(category);
        }
//        payment.setUpdatedOn()
        return setPaymentResponse(paymentRepository.save(payment));
    }

    public void deletePaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(String.format(PAYMENT_NOT_FOUND_MESSAGE, id)));
        paymentRepository.delete(payment);
    }

    public void saveAllPayments(List<CreatePaymentCommand> commandsList) {
        List<Payment> paymentsList = new ArrayList<>();
        commandsList.forEach(command -> paymentsList.add(preparePaymentToSave(command)));
        paymentRepository.saveAll(paymentsList);
    }

    public List<PaymentResponse> findAllByUserForYear(Long userId, int year) {
        List<Payment> payments = paymentRepository.findAllByUserForYear(userId, year);
        return payments.stream().map(this::setPaymentResponse).toList();
    }

    public List<PaymentResponse> findAllByUserForYearAndMonth(Long userId, int year, int month) {
        List<Payment> payments = paymentRepository.findAllByUserForYearAndMonth(userId, year, month);
        return payments.stream().map(this::setPaymentResponse).toList();
    }

    public PaymentReportResponse calculateYearlyByUserAndCategory(Long userId, int year) {
        List<PaymentReportResponseParameters> reportResponses = paymentRepository.calculateYearlyByUserAndCategory(userId, year);
        return getPaymentReportResponse(reportResponses);
    }

    public PaymentReportResponse calculateMonthlyByUserAndCategory(Long userId, int year, int month) {
        List<PaymentReportResponseParameters> reportResponses = paymentRepository.calculateMonthlyByUserAndCategory(userId, year, month);
        return getPaymentReportResponse(reportResponses);
    }

    private PaymentResponse setPaymentResponse(Payment payment) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setTitle(payment.getTitle());
        paymentResponse.setDescription(payment.getDescription());
        paymentResponse.setAmount(payment.getAmount());
        paymentResponse.setCategoryName(payment.getCategory().getName());
        paymentResponse.setCreatedOn(payment.getCreatedOn());
//        paymentResponse.setUpdatedOn(payment.getUpdatedOn());
        return paymentResponse;
    }

    private PaymentReportResponse getPaymentReportResponse(List<PaymentReportResponseParameters> reportResponses) {
        Map<String, BigDecimal> result = new HashMap<>();
        reportResponses.forEach(parameter -> result.put(parameter.getCategory(), parameter.getAmount()));
        return new PaymentReportResponse(result);
    }

    private Payment preparePaymentToSave(CreatePaymentCommand command) {
        Payment payment = new Payment();
        Category category = categoryService.findCategoryById(command.getCategoryId());
        User user = userService.findUserById(command.getUserId());
        payment.setUser(user);
        payment.setAmount(command.getAmount());
        payment.setCreatedOn(command.getCreatedOn());
        payment.setCategory(category);
        payment.setTitle(command.getTitle());
        payment.setDescription(command.getDescription());
        return payment;
    }
}
