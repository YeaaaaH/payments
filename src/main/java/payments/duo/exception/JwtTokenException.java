package payments.duo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class JwtTokenException extends RuntimeException {
    public JwtTokenException(String exception) {
        super(exception);
    }
}
