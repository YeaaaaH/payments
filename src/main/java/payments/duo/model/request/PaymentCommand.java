package payments.duo.model.request;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class PaymentCommand {

    private BigDecimal amount;
    private Long categoryId;


    public PaymentCommand(BigDecimal amount, Long categoryId) {
        this.amount = amount;
        this.categoryId = categoryId;
    }
}
