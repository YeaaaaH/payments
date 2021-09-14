package payments.duo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import payments.duo.model.Payment;
import payments.duo.model.response.PaymentReportResponseParameters;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query(value = "SELECT * from payments p WHERE p.user_id = :userId and EXTRACT (YEAR FROM p.created_on) = :year",
            nativeQuery = true)
    List<Payment> findAllByUserForYear(Long userId, int year);

    @Query(value = "SELECT * from payments p WHERE p.user_id = :userId and EXTRACT (YEAR FROM p.created_on) = :year " +
            "AND EXTRACT (month FROM p.created_on) = :month", nativeQuery = true)
    List<Payment> findAllByUserForYearAndMonth(Long userId, int year, int month);

    @Query(value = "select new payments.duo.model.response.PaymentReportResponseParameters(c.name, sum(p.amount)) " +
            "from Payment p join Category c on p.category.id = c.id " +
            "where p.user.id = :userId and function('YEAR', p.createdOn) = :year group by c.name")
    List<PaymentReportResponseParameters> calculateYearlyByUserAndCategory(Long userId, int year);

    @Query(value = "select new payments.duo.model.response.PaymentReportResponseParameters(c.name, sum(p.amount)) " +
            "from Payment p join Category c on p.category.id = c.id " +
            "where p.user.id = :userId and function('YEAR', p.createdOn) = :year AND function('MONTH', p.createdOn) = :month " +
            "group by c.name")
    List<PaymentReportResponseParameters> calculateMonthlyByUserAndCategory(Long userId, int year, int month);
}
