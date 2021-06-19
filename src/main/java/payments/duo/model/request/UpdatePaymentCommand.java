package payments.duo.model.request;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UpdatePaymentCommand extends PaymentCommand {

    private Long id;
    private String description;

    public UpdatePaymentCommand(BigDecimal amount, Long categoryId, String title, Long id, String description) {
        super(amount, categoryId, title);
        this.id = id;
        this.description = description;
    }
}
