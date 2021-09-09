package payments.duo.service;

import org.springframework.stereotype.Service;
import payments.duo.exception.CategoryNotFoundException;
import payments.duo.exception.PaymentNotFoundException;
import payments.duo.model.Category;
import payments.duo.model.Payment;
import payments.duo.model.auth.User;
import payments.duo.model.request.CreatePaymentCommand;
import payments.duo.model.request.UpdatePaymentCommand;
import payments.duo.repository.CategoryRepository;
import payments.duo.repository.PaymentRepository;

import javax.transaction.Transactional;

@Service
@Transactional
public class PaymentService {

    private final UserService userService;
    private final PaymentRepository paymentRepository;
    private final CategoryRepository categoryRepository;

    public PaymentService(UserService userService, PaymentRepository paymentRepository, CategoryRepository categoryRepository) {
        this.userService = userService;
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
        payment.setTitle(command.getTitle());
        payment.setDescription(command.getDescription());
        User user = userService.findUserById(command.getUserId());
        payment.setUser(user);
        return paymentRepository.save(payment);
    }

    public Payment updatePayment(UpdatePaymentCommand command) {
        Payment payment = paymentRepository.findById(command.getId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment with id: " + command.getId() + " hasn't been found"));
        payment.setAmount(command.getAmount());
        payment.setDescription(command.getDescription());
        payment.setTitle(command.getTitle());
        return paymentRepository.save(payment);
    }

    public void deletePaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with id: " + id + " hasn't been found"));
        paymentRepository.delete(payment);
    }
}
