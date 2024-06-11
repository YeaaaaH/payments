package payments.duo.controller;

import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public PaymentResponse getPaymentById(@PathVariable Long id) {
        return paymentService.findPaymentById(id);
    }

    @PostMapping
    public PaymentResponse savePayment(@Valid @RequestBody CreatePaymentCommand command) {
        return paymentService.savePayment(command);
    }

    @PostMapping("saveAll")
    public ResponseEntity<String> saveAllPayments(@Valid @RequestBody CreatePaymentsListCommand command) {
        paymentService.saveAllPayments(command.getPaymentCommands());
        return new ResponseEntity<>("Batch save processed successfully.", HttpStatus.OK);
    }

    @PutMapping("{id}")
    public PaymentResponse updatePayment(@RequestBody UpdatePaymentCommand command, @PathVariable Long id) {
        return paymentService.updatePayment(command, id);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePaymentById(@PathVariable Long id) {
        paymentService.deletePaymentById(id);
        return new ResponseEntity<>("Payment with id:" + id + " had been deleted", HttpStatus.OK);
    }

    @GetMapping("/list/yearly")
    public List<PaymentResponse> findAllByUserForYear(@RequestParam Long userId, @RequestParam int year) {
        return paymentService.findAllByUserForYear(userId, year);
    }

    @GetMapping("/list/monthly")
    public List<PaymentResponse> findAllByUserForYearAndMonth(@RequestParam Long userId, @RequestParam int year, @RequestParam int month) {
        return paymentService.findAllByUserForYearAndMonth(userId, year, month);
    }

    @GetMapping("/report/yearly")
    public PaymentReportResponse calculateYearlyByUserAndCategory(@RequestParam Long userId, @RequestParam int year) {
        return paymentService.calculateYearlyByUserAndCategory(userId, year);
    }

    @GetMapping("/report/monthly")
    public PaymentReportResponse calculateMonthlyByUserAndCategory(@RequestParam Long userId, @RequestParam int year, @RequestParam int month) {
        return paymentService.calculateMonthlyByUserAndCategory(userId, year, month);
    }
}
