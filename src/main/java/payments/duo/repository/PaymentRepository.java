package payments.duo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import payments.duo.model.Payment;
import payments.duo.model.response.PaymentReportResponseParameters;

import java.time.Instant;
import java.util.List;

/**
 * Periods are half-open ranges [from, to): the service computes the boundaries of a year/month
 * in the app time zone, so a payment at 00:30 local time lands in the right month.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select p from Payment p where p.user.id = :userId and p.paidAt >= :from and p.paidAt < :to")
    List<Payment> findAllByUserForPeriod(@Param("userId") Long userId,
                                         @Param("from") Instant from,
                                         @Param("to") Instant to);

    @Query("select new payments.duo.model.response.PaymentReportResponseParameters(c.name, sum(p.amount)) " +
            "from Payment p join Category c on p.category.id = c.id " +
            "where p.user.id = :userId and p.paidAt >= :from and p.paidAt < :to group by c.name")
    List<PaymentReportResponseParameters> calculateByUserAndCategoryForPeriod(@Param("userId") Long userId,
                                                                              @Param("from") Instant from,
                                                                              @Param("to") Instant to);
}
