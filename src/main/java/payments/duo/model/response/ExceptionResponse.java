package payments.duo.model.response;

import lombok.Getter;
import payments.duo.exception.ExceptionDetails;

import java.util.List;

@Getter
public class ExceptionResponse {
    private final List<ExceptionDetails> exceptions;
    public ExceptionResponse(List<ExceptionDetails> exceptions) {
        this.exceptions = exceptions;
    }
}
