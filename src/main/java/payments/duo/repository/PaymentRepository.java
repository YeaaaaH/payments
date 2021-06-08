package payments.duo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payments.duo.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
