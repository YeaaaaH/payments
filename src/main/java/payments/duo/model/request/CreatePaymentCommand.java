package payments.duo.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class CreatePaymentCommand extends PaymentCommand {
    @NotNull
    private Instant paidAt;

    public CreatePaymentCommand(BigDecimal amount, Long categoryId, String title, String description, Long userId, Instant paidAt) {
        super(amount, categoryId, title, description, userId);
        this.paidAt = paidAt;
    }
}
