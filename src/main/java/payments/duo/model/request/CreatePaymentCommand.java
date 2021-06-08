package payments.duo.model.request;

import lombok.Getter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
public class CreatePaymentCommand {

    private BigDecimal amount;
    private Date createdOn;
    private Long categoryId;

    public CreatePaymentCommand(BigDecimal amount, Date createdOn, Long categoryId) {
        this.amount = amount;
        this.createdOn = createdOn;
        this.categoryId = categoryId;
    }
}
