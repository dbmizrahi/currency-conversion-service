package co.mizrahi.currency.conversion.services;

import co.mizrahi.currency.conversion.clients.CoinbaseFeignClient;
import co.mizrahi.currency.conversion.models.CurrencyConversionResponse;
import co.mizrahi.currency.conversion.models.ExchangeRates;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    public static final String WRONG_CURRENCY_CODE_FOR = "Wrong currency code for: {}";

    private final CoinbaseFeignClient coinbaseFeignClient;
    private final AuthenticationService authenticationService;

    @Override
    public CurrencyConversionResponse getConversionResponse(String from, String to, BigDecimal amount) {
        ExchangeRates rates;
        try {
            rates = this.getExchangeRates(from);
        } catch (Exception e) {
            log.error(WRONG_CURRENCY_CODE_FOR, from);
            throw new RuntimeException(e);
        }
        BigDecimal rate;
        try {
            rate = rates.getData().getRates().get(to);
        } catch (Exception e) {
            log.error(WRONG_CURRENCY_CODE_FOR, to);
            throw new RuntimeException(e);
        }
        return CurrencyConversionResponse.builder()
                .from(from)
                .to(to)
                .amount(amount)
                .result(amount.multiply(rate))
                .build();
    }

    @SneakyThrows
    private ExchangeRates getExchangeRates(String currencyCode) {
        String jwtToken = this.authenticationService.authenticate();
        return this.coinbaseFeignClient.getExchangeRates("Bearer " + jwtToken, currencyCode);
    }
}
