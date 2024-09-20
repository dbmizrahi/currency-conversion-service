package co.mizrahi.currency.conversion.services;

import co.mizrahi.currency.conversion.clients.CoinbaseFeignClient;
import co.mizrahi.currency.conversion.entities.UserKey;
import co.mizrahi.currency.conversion.entities.UserRequestLog;
import co.mizrahi.currency.conversion.models.CurrencyConversionResponse;
import co.mizrahi.currency.conversion.models.ExchangeRates;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    public static final String PLACEHOLDER = "{}";
    public static final String CURRENCY_CODE_FOR = "Wrong currency code for: ";
    public static final String WRONG_CURRENCY_CODE_FOR = CURRENCY_CODE_FOR + PLACEHOLDER;

    private final CoinbaseFeignClient coinbaseFeignClient;
    private final AuthenticationService authenticationService;

    @Override
    @SneakyThrows
    public CurrencyConversionResponse getConversionResponse(String from, String to, BigDecimal amount) {
        ExchangeRates rates;
        try {
            rates = this.getExchangeRates(from);
        } catch (Exception e) {
            log.error(WRONG_CURRENCY_CODE_FOR, from);
            throw new RuntimeException(e);
        }
        BigDecimal rate = Optional.ofNullable(rates.getData().getRates().get(to))
                .orElseThrow(() -> new BadRequestException(CURRENCY_CODE_FOR +  to));
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
