package co.mizrahi.currency.conversion.config;

import co.mizrahi.currency.conversion.entities.UserKey;
import co.mizrahi.currency.conversion.repositories.UserKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/public")) {
            filterChain.doFilter(request, response);
            return;
        }
        String apiKey = request.getHeader("X-Api-Key");
        if (apiKey == null || this.validateApiKey(apiKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing API key");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean validateApiKey(String apiKey) {
        UserKey userKey = this.userKeyRepository.findByApiKey(apiKey);
        return isNull(userKey);
    }
}
