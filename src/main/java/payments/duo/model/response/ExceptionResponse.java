package payments.duo.model.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import payments.duo.exception.ExceptionDetails;

import java.util.List;

@Data
public class ExceptionResponse {
    private final List<ExceptionDetails> exceptions;

    public ExceptionResponse(List<ExceptionDetails> exceptions) {
        this.exceptions = exceptions;
    }

    public ExceptionResponse() {
        exceptions = null;
    }
}
