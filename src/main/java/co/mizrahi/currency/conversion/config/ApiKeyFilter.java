package co.mizrahi.currency.conversion.config;

import co.mizrahi.currency.conversion.entities.ApiRequestLog;
import co.mizrahi.currency.conversion.entities.UserKey;
import co.mizrahi.currency.conversion.entities.UserRequestLog;
import co.mizrahi.currency.conversion.repositories.ApiRequestLogRepository;
import co.mizrahi.currency.conversion.repositories.UserKeyRepository;
import co.mizrahi.currency.conversion.repositories.UserRequestLogRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Component
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private final UserKeyRepository userKeyRepository;
    private final ApiRequestLogRepository apiRequestLogRepository;

    @Value("${limit.weekday}")
    private int weekdayLimit;

    @Value("${limit.weekend}")
    private int weekendLimit;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        logger.info("Incoming request path: " + path);
        if (path.startsWith("/public")) {
            filterChain.doFilter(request, response);
            return;
        }
        String apiKey = request.getHeader("X-Api-Key");
        UserKey userKey = this.userKeyRepository.findByApiKey(apiKey);
        if (apiKey == null || isNull(userKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing API key");
            return;
        }
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
        int maxRequests = isWeekend ? weekendLimit : weekdayLimit;
        Optional<ApiRequestLog> log = this.apiRequestLogRepository.findByApiKeyAndRequestDate(apiKey, today);
        int requestCount = log.map(ApiRequestLog::getRequestCount).orElse(0);
        if (requestCount >= maxRequests) {
            logger.warn("API key has exceeded the allowed limit of " + maxRequests + " requests");
            response.sendError(HttpServletResponse.SC_GONE, "Request limit exceeded");
            return;
        }
        ApiRequestLog requestLog = log.orElse(ApiRequestLog.builder()
                .apiKey(apiKey)
                .requestDate(today)
                .requestCount(0)
                .build());
        requestLog.setRequestCount(requestLog.getRequestCount() + 1);
        apiRequestLogRepository.save(requestLog);
        logger.info("API Key is valid and request count is within the limit");
        filterChain.doFilter(request, response);
    }
}
