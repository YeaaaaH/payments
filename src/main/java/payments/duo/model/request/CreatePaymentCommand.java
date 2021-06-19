package payments.duo.model.request;

import lombok.Getter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
public class CreatePaymentCommand extends PaymentCommand {

    private Date createdOn;

    public CreatePaymentCommand(BigDecimal amount, Long categoryId, String title, Date createdOn) {
        super(amount, categoryId, title);
        this.createdOn = createdOn;
    }
}
