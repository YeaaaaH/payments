package payments.duo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseBuilder {
    public static void buildResponse(HttpServletResponse response, Object body) throws IOException {
        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}