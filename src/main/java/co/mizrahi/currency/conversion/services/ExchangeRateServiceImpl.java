package co.mizrahi.currency.conversion.services;

import co.mizrahi.currency.conversion.clients.CoinbaseFeignClient;
import co.mizrahi.currency.conversion.models.CurrencyConversionResponse;
import co.mizrahi.currency.conversion.models.ExchangeRates;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final CoinbaseFeignClient coinbaseFeignClient;
    private final AuthenticationService authenticationService;

    @Override
    public CurrencyConversionResponse getConversionResponse(String from, String to, BigDecimal amount) {
        var rates = this.getExchangeRates(from);
        return null;
    }

    @SneakyThrows
    private ExchangeRates getExchangeRates(String currencyCode) {
        String jwtToken = this.authenticationService.authenticate();
        return this.coinbaseFeignClient.getExchangeRates("Bearer " + jwtToken, currencyCode);
    }
}
