package payments.duo.exception;

import lombok.Getter;

@Getter
public class ExceptionDetails {
    private String message;

    public ExceptionDetails(String message) {
        this.message = message;
    }
}