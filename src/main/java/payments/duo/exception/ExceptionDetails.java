package payments.duo.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExceptionDetails {
    private String message;

    public ExceptionDetails(String message) {
        this.message = message;
    }
}