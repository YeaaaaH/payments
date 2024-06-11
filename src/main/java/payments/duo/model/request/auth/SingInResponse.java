package payments.duo.model.request.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class SingInResponse {
    String accessToken;
    Long userId;
    List<String> roles;
}
