package payments.duo.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import payments.duo.model.response.ExceptionResponse;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomizedEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex) {
        return new ResponseEntity<>(prepareExceptions(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                     HttpStatus status, WebRequest request) {
        List<ExceptionDetails> exceptions = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String[] message = {fieldError.getField(), fieldError.getDefaultMessage()};
            exceptions.add(new ExceptionDetails(String.join(" ", message)));
        }
        ExceptionResponse exceptionResponse = new ExceptionResponse(exceptions);
        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handlePaymentNotFoundException(PaymentNotFoundException ex) {
        return new ResponseEntity<>(prepareExceptions(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return new ResponseEntity<>(prepareExceptions(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(prepareExceptions(ex), HttpStatus.BAD_REQUEST);
    }

    public static ExceptionResponse prepareExceptions(Exception exception) {
        List<ExceptionDetails> exceptions = List.of(
                new ExceptionDetails(exception.getMessage())
        );
        return new ExceptionResponse(exceptions);
    }
}