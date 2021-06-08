package payments.duo.model.response;

import lombok.Getter;

@Getter
public class ExceptionResponse {
    private String code;
    private String message;

    public ExceptionResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
