package payments.duo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import payments.duo.model.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query(value = "SELECT * from payments p WHERE p.user_id = :userId and EXTRACT (YEAR FROM p.created_on) = :year",
            nativeQuery = true)
    List<Payment> findAllByUserForYear(Long userId, int year);

    @Query(value = "SELECT * from payments p WHERE p.user_id = :userId and EXTRACT (YEAR FROM p.created_on) = :year " +
            "AND EXTRACT (month FROM p.created_on) = :month", nativeQuery = true)
    List<Payment> findAllByUserForYearAndMonth(Long userId, int year, int month);
}
