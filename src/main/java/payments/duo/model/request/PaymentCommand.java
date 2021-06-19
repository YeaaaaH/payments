package payments.duo.model.request;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class PaymentCommand {

    private BigDecimal amount;
    private Long categoryId;
    private String title;

    public PaymentCommand(BigDecimal amount, Long categoryId, String title) {
        this.amount = amount;
        this.categoryId = categoryId;
        this.title = title;
    }
}
