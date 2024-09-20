package co.mizrahi.currency.conversion.logging;

import co.mizrahi.currency.conversion.entities.UserKey;
import co.mizrahi.currency.conversion.entities.UserRequestLog;
import co.mizrahi.currency.conversion.models.CurrencyConversionResponse;
import co.mizrahi.currency.conversion.repositories.UserRequestLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Created at 20/09/2024
 *
 * @author David Mizrahi
 */
@Service("userLogger")
@RequiredArgsConstructor
public class UserRequestLoggingService implements RequestLoggingService<CurrencyConversionResponse> {

    private final UserRequestLogRepository userRequestLogRepository;

    @Override
    public void logRequest(CurrencyConversionResponse body) {
        var userKey = (UserKey) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserRequestLog userRequestLog = UserRequestLog.builder()
                .currencyConversionResponse(body)
                .timestamp(LocalDateTime.now())
                .username(userKey.getEmail())
                .build();
        this.userRequestLogRepository.save(userRequestLog);
    }
}
