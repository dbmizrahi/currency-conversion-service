package co.mizrahi.currency.conversion.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConversionResponse {
    private String from;
    private String to;
    private BigDecimal amount;
    private BigDecimal result;
}
