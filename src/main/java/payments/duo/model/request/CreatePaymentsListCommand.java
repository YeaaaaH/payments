package payments.duo.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreatePaymentsListCommand {
    @Valid
    List<CreatePaymentCommand> paymentCommands;
}
