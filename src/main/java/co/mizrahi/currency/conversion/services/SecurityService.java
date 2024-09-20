package co.mizrahi.currency.conversion.services;

import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 20/09/2024
 *
 * @author David Mizrahi
 */
public interface SecurityService {
    boolean validateApiKey(@NotNull HttpServletResponse response, String apiKey);
    boolean validateRateLimit(@NotNull HttpServletResponse response, String apiKey);
}
