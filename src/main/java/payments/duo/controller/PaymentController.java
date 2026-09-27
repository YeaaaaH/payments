package payments.duo.controller;

import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import payments.duo.model.request.CreatePaymentCommand;
import payments.duo.model.request.CreatePaymentsListCommand;
import payments.duo.model.request.UpdatePaymentCommand;
import payments.duo.model.response.PaymentReportResponse;
import payments.duo.model.response.PaymentResponse;
import payments.duo.security.AuthenticatedUser;
import payments.duo.service.PaymentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/payment")
@Api(description="Operations related to payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("{id}")
    public PaymentResponse getPaymentById(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) {
        return paymentService.findPaymentById(user.id(), id);
    }

    @PostMapping
    public PaymentResponse savePayment(@AuthenticationPrincipal AuthenticatedUser user,
                                       @Valid @RequestBody CreatePaymentCommand command) {
        return paymentService.savePayment(user.id(), command);
    }

    @PostMapping("saveAll")
    public ResponseEntity<String> saveAllPayments(@AuthenticationPrincipal AuthenticatedUser user,
                                                  @Valid @RequestBody CreatePaymentsListCommand command) {
        paymentService.saveAllPayments(user.id(), command.getPaymentCommands());
        return new ResponseEntity<>("Batch save processed successfully.", HttpStatus.OK);
    }

    @PutMapping("{id}")
    public PaymentResponse updatePayment(@AuthenticationPrincipal AuthenticatedUser user,
                                         @Valid @RequestBody UpdatePaymentCommand command, @PathVariable Long id) {
        return paymentService.updatePayment(user.id(), command, id);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePaymentById(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) {
        paymentService.deletePaymentById(user.id(), id);
        return new ResponseEntity<>("Payment with id:" + id + " had been deleted", HttpStatus.OK);
    }

    @GetMapping("/list/yearly")
    public List<PaymentResponse> findAllByUserForYear(@AuthenticationPrincipal AuthenticatedUser user, @RequestParam int year) {
        return paymentService.findAllByUserForYear(user.id(), year);
    }

    @GetMapping("/list/monthly")
    public List<PaymentResponse> findAllByUserForYearAndMonth(@AuthenticationPrincipal AuthenticatedUser user, @RequestParam int year, @RequestParam int month) {
        return paymentService.findAllByUserForYearAndMonth(user.id(), year, month);
    }

    @GetMapping("/report/yearly")
    public PaymentReportResponse calculateYearlyByUserAndCategory(@AuthenticationPrincipal AuthenticatedUser user, @RequestParam int year) {
        return paymentService.calculateYearlyByUserAndCategory(user.id(), year);
    }

    @GetMapping("/report/monthly")
    public PaymentReportResponse calculateMonthlyByUserAndCategory(@AuthenticationPrincipal AuthenticatedUser user, @RequestParam int year, @RequestParam int month) {
        return paymentService.calculateMonthlyByUserAndCategory(user.id(), year, month);
    }
}
