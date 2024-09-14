package co.mizrahi.currency.conversion.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created at 14/09/2024
 * Model for Exchange Rates response from Coinbase API.
 *
 * @author David Mizrahi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRates {

    private ResponseData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseData {
        private String currency;
        private Map<String, BigDecimal> rates;
    }
}
