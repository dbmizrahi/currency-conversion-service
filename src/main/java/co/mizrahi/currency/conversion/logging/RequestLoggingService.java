package co.mizrahi.currency.conversion.logging;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Created at 20/09/2024
 *
 * @author David Mizrahi
 */
public interface RequestLoggingService<T> {
    void logRequest(T body);
}
