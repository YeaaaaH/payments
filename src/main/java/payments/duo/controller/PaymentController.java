package payments.duo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payments.duo.model.Payment;
import payments.duo.model.request.CreatePaymentCommand;
import payments.duo.model.request.UpdatePaymentCommand;
import payments.duo.service.PaymentService;

@RestController
@RequestMapping("api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("{id}")
    public Payment getPaymentById(@PathVariable long id) {
        return paymentService.findPaymentById(id);
    }

    @PostMapping
    public Payment savePayment(@RequestBody CreatePaymentCommand command) {
        return paymentService.savePayment(command);
    }

    @PutMapping
    public Payment updatePayment(@RequestBody UpdatePaymentCommand command) {
        return paymentService.updatePayment(command);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePaymentById(@PathVariable long id) {
        paymentService.deletePaymentById(id);
        return new ResponseEntity<>("Payment with id:" + id + " deleted", HttpStatus.OK);
    }
}
