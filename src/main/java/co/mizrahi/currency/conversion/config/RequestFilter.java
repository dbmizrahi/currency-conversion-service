package co.mizrahi.currency.conversion.config;

import co.mizrahi.currency.conversion.logging.RequestLoggingService;
import co.mizrahi.currency.conversion.models.CurrencyConversionResponse;
import co.mizrahi.currency.conversion.services.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RequestFilter extends OncePerRequestFilter {

    private final SecurityService securityService;
    private final Map<String, RequestLoggingService> loggingServices;

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
        if (!this.securityService.validateApiKey(response, apiKey)) return;
        if (!this.securityService.validateRateLimit(response, apiKey)) return;
        filterChain.doFilter(request, response);
    }
}
