package payments.duo.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
public class CreatePaymentCommand extends PaymentCommand {

    private Date createdOn;

    public CreatePaymentCommand(BigDecimal amount, Long categoryId, String title, String description, Long userId, Date createdOn) {
        super(amount, categoryId, title, description, userId);
        this.createdOn = createdOn;
    }
}
