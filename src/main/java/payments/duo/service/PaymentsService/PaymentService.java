package payments.duo.service.PaymentsService;

import org.springframework.stereotype.Service;
import payments.duo.exception.CategoryNotFoundException;
import payments.duo.exception.PaymentNotFoundException;
import payments.duo.model.Category;
import payments.duo.model.Payment;
import payments.duo.model.request.CreatePaymentCommand;
import payments.duo.repository.CategoryRepository;
import payments.duo.repository.PaymentRepository;

import javax.transaction.Transactional;

@Service
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final CategoryRepository categoryRepository;

    public PaymentService(PaymentRepository paymentRepository, CategoryRepository categoryRepository) {
        this.paymentRepository = paymentRepository;
        this.categoryRepository = categoryRepository;
    }

    public Payment findPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with id: " + id + " hasn't been found"));
    }

    public Payment savePayment(CreatePaymentCommand command) {
        Payment payment = new Payment();
        Category category = categoryRepository.findById(command.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category with id: " + command.getCategoryId() + " hasn't been found"));
        payment.setAmount(command.getAmount());
        payment.setCreatedOn(command.getCreatedOn());
        payment.setCategory(category);
        return paymentRepository.save(payment);
    }
}
