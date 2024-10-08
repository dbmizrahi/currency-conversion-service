package co.mizrahi.currency.conversion.services;

import co.mizrahi.currency.conversion.models.CurrencyConversionResponse;

import java.math.BigDecimal;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
public interface ExchangeRateService {
    CurrencyConversionResponse getConversionResponse(String from, String to, BigDecimal amount);
}
