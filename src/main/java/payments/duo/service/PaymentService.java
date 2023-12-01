package payments.duo.service;

import payments.duo.model.request.CreatePaymentCommand;
import payments.duo.model.request.UpdatePaymentCommand;
import payments.duo.model.response.PaymentReportResponse;
import payments.duo.model.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse findPaymentById(Long id);
    PaymentResponse savePayment(CreatePaymentCommand command);
    PaymentResponse updatePayment(UpdatePaymentCommand command, Long id);
    void deletePaymentById(Long id);
    void saveAllPayments(List<CreatePaymentCommand> commandsList);
    List<PaymentResponse> findAllByUserForYear(Long userId, int year);
    List<PaymentResponse> findAllByUserForYearAndMonth(Long userId, int year, int month);
    PaymentReportResponse calculateYearlyByUserAndCategory(Long userId, int year);
    PaymentReportResponse calculateMonthlyByUserAndCategory(Long userId, int year, int month);
}
