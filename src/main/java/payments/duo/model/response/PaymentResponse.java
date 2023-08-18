package payments.duo.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String title;
    private String description;
    private BigDecimal amount;
    private String categoryName;
    private LocalDate createdOn;
    private LocalDate updatedOn;
}
