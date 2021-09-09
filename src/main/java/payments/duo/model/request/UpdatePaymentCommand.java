package payments.duo.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePaymentCommand extends PaymentCommand {

    private Long id;

    public UpdatePaymentCommand(BigDecimal amount, Long categoryId, String title, Long userId, Long id, String description) {
        super(amount, categoryId, title, description, userId);
        this.id = id;
    }
}
