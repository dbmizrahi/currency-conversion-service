package co.mizrahi.currency.conversion.config;

import co.mizrahi.currency.conversion.entities.ApiRequestLog;
import co.mizrahi.currency.conversion.entities.UserKey;
import co.mizrahi.currency.conversion.repositories.ApiRequestLogRepository;
import co.mizrahi.currency.conversion.repositories.UserKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private final UserKeyRepository userKeyRepository;
    private final ApiRequestLogRepository apiRequestLogRepository;

    @Value("${request.limit.weekday}")
    private int weekdayLimit;

    @Value("${request.limit.weekend}")
    private int weekendLimit;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        log.info("Incoming request path: {}", path);
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

        Optional<ApiRequestLog> requestLog = this.apiRequestLogRepository.findByApiKeyAndRequestDate(apiKey, today);
        Integer requestCount = requestLog.map(ApiRequestLog::getRequestCount).orElse(0);

        if (requestCount >= maxRequests) {
            log.warn("API key has exceeded the allowed limit of {} requests", maxRequests);
            response.sendError(HttpServletResponse.SC_GONE, "Request limit exceeded. Request count: " + requestCount);
            return;
        }
        ApiRequestLog requestLogUpdate = requestLog.orElse(ApiRequestLog.builder()
                .apiKey(apiKey)
                .requestDate(today)
                .requestCount(0)
                .build());
        requestLogUpdate.setRequestCount(requestLogUpdate.getRequestCount() + 1);
        this.apiRequestLogRepository.save(requestLogUpdate);
        log.info("API Key is valid and request count is within the limit: {}", requestCount);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                apiKey, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
