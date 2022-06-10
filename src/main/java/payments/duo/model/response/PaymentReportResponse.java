package payments.duo.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
//TODO refactor from two lists into map
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReportResponse {
    List<String> categories;
    List<BigDecimal> totals;
}
