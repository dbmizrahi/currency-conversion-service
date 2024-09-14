package co.mizrahi.currency.conversion.services;

import org.springframework.http.ResponseEntity;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
public interface RegistrationService {
    ResponseEntity<String> register(String email);
}
