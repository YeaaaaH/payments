package payments.duo.service;

import payments.duo.model.request.CreatePaymentCommand;
import payments.duo.model.request.UpdatePaymentCommand;
import payments.duo.model.response.PaymentReportResponse;
import payments.duo.model.response.PaymentResponse;

import java.util.List;

/**
 * Every method works only with payments of the given user.
 * Another user's payment is treated as missing (PaymentNotFoundException).
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
