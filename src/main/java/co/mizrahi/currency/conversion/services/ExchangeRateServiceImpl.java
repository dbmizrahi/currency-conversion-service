package co.mizrahi.currency.conversion.services;

import co.mizrahi.currency.conversion.clients.CoinbaseFeignClient;
import co.mizrahi.currency.conversion.entities.UserKey;
import co.mizrahi.currency.conversion.entities.UserRequestLog;
import co.mizrahi.currency.conversion.models.CurrencyConversionResponse;
import co.mizrahi.currency.conversion.models.ExchangeRates;
import co.mizrahi.currency.conversion.repositories.UserKeyRepository;
import co.mizrahi.currency.conversion.repositories.UserRequestLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    private final UserKeyRepository userKeyRepository;
    private final CoinbaseFeignClient coinbaseFeignClient;
    private final AuthenticationService authenticationService;
    private final UserRequestLogRepository userRequestLogRepository;

    @Override
    public CurrencyConversionResponse getConversionResponse(String from, String to, BigDecimal amount, String apiKey) {
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
        CurrencyConversionResponse currencyConversionResponse = CurrencyConversionResponse.builder()
                .from(from)
                .to(to)
                .amount(amount)
                .result(amount.multiply(rate))
                .build();
        UserKey userKey = this.userKeyRepository.findByApiKey(apiKey);
        UserRequestLog userRequestLog = UserRequestLog.builder()
                .currencyConversionResponse(currencyConversionResponse)
                .timestamp(LocalDateTime.now())
                .username(userKey.getEmail())
                .build();
        this.userRequestLogRepository.save(userRequestLog);
        return currencyConversionResponse;
    }

    @SneakyThrows
    private ExchangeRates getExchangeRates(String currencyCode) {
        String jwtToken = this.authenticationService.authenticate();
        return this.coinbaseFeignClient.getExchangeRates("Bearer " + jwtToken, currencyCode);
    }
}
