package payments.duo.model.request;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UpdatePaymentCommand extends PaymentCommand {

    private Long id;

    public UpdatePaymentCommand(BigDecimal amount, Long categoryId, Long id) {
        super(amount, categoryId);
        this.id = id;
    }
}
