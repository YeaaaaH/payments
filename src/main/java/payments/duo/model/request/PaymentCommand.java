package payments.duo.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

import static payments.duo.utils.Constants.VALID_PAYMENT_AMOUNT_MESSAGE;

@Getter
@Setter
@NoArgsConstructor
public class PaymentCommand {

    @NotNull
    @Positive(message = VALID_PAYMENT_AMOUNT_MESSAGE)
    private BigDecimal amount;
    @NotNull
    private Long categoryId;
    private String title;
    private String description;
    @NotNull
    private Long userId;

    public PaymentCommand(BigDecimal amount, Long categoryId, String description, String title, Long userId) {
        this.amount = amount;
        this.categoryId = categoryId;
        this.title = title;
        this.userId = userId;
        this.description = description;
    }
}
