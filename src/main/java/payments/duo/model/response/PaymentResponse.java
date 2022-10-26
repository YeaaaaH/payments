package payments.duo.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
//TODO change date to timestamp
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String title;
    private String description;
    private BigDecimal amount;
    private String categoryName;
    private Date createdOn;
    private Date updatedOn;
}
