package payments.duo.service;

import payments.duo.model.request.CreatePaymentCommand;
import payments.duo.model.request.UpdatePaymentCommand;
import payments.duo.model.response.PaymentReportResponse;
import payments.duo.model.response.PaymentResponse;

import java.util.List;

/**
 * Single-payment operations only see payments of {@code userId}; another user's payment is reported as not found.
 */
/**
 * Operations act on the payments of {@code userId} only; another user's payment is reported as not found.
 */
public interface PaymentService {
    PaymentResponse findPaymentById(Long userId, Long id);
    PaymentResponse savePayment(Long userId, CreatePaymentCommand command);
    PaymentResponse updatePayment(Long userId, UpdatePaymentCommand command, Long id);
    void deletePaymentById(Long userId, Long id);
    void saveAllPayments(Long userId, List<CreatePaymentCommand> commandsList);
    List<PaymentResponse> findAllByUserForYear(Long userId, int year);
    List<PaymentResponse> findAllByUserForYearAndMonth(Long userId, int year, int month);
    PaymentReportResponse calculateYearlyByUserAndCategory(Long userId, int year);
    PaymentReportResponse calculateMonthlyByUserAndCategory(Long userId, int year, int month);
}
