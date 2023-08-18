package payments.duo.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CreatePaymentCommand extends PaymentCommand {
    @NotNull
    private LocalDate createdOn;

    public CreatePaymentCommand(BigDecimal amount, Long categoryId, String title, String description, Long userId, LocalDate createdOn) {
        super(amount, categoryId, title, description, userId);
        this.createdOn = createdOn;
    }
}
