package co.mizrahi.currency.conversion.config;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
import co.mizrahi.currency.conversion.repositories.UserKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private final UserKeyRepository userKeyRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader("X-Api-Key");

        if (apiKey == null || !validateApiKey(apiKey)) {
            // Invalid or missing API key; reject the request
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing API key");
            return;
        }

        // Proceed with the next filter if the API key is valid
        filterChain.doFilter(request, response);
    }

    private boolean validateApiKey(String apiKey) {
        // Query the database for the API key
        return this.userKeyRepository.findByApiKey(apiKey) != null;
    }
}
