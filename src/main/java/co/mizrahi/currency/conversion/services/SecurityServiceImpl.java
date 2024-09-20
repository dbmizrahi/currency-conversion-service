package co.mizrahi.currency.conversion.services;

import co.mizrahi.currency.conversion.entities.ApiRequestLog;
import co.mizrahi.currency.conversion.entities.UserKey;
import co.mizrahi.currency.conversion.repositories.ApiRequestLogRepository;
import co.mizrahi.currency.conversion.repositories.UserKeyRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Created at 20/09/2024
 *
 * @author David Mizrahi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    @Value("${request.limit.weekday}")
    private int weekdayLimit;

    @Value("${request.limit.weekend}")
    private int weekendLimit;

    private final UserKeyRepository userKeyRepository;
    private final ApiRequestLogRepository apiRequestLogRepository;

    @Override
    @SneakyThrows
    public boolean validateApiKey(@NotNull HttpServletResponse response, String apiKey) {
        UserKey userKey = this.userKeyRepository.findByApiKey(apiKey);
        if (apiKey == null || isNull(userKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing API key");
            return false;
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userKey, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return true;
    }

    @Override
    @SneakyThrows
    public boolean validateRateLimit(@NotNull HttpServletResponse response, String apiKey) {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
        int maxRequests = isWeekend ? weekendLimit : weekdayLimit;

        Optional<ApiRequestLog> requestLog = this.apiRequestLogRepository.findByApiKeyAndRequestDate(apiKey, today);
        Integer requestCount = requestLog.map(ApiRequestLog::getRequestCount).orElse(0);

        if (requestCount >= maxRequests) {
            log.warn("API key has exceeded the allowed limit of {} requests", maxRequests);
            response.sendError(HttpServletResponse.SC_GONE, "Request limit exceeded. Request count: " + requestCount);
            return false;
        }
        ApiRequestLog requestLogUpdate = requestLog.orElse(ApiRequestLog.builder()
                .apiKey(apiKey)
                .requestDate(today)
                .requestCount(0)
                .build());
        requestLogUpdate.setRequestCount(requestLogUpdate.getRequestCount() + 1);
        this.apiRequestLogRepository.save(requestLogUpdate);
        log.info("API Key is valid and request count is within the limit: {}", requestCount);
        return true;
    }
}
