package co.mizrahi.currency.conversion.controllers;

import co.mizrahi.currency.conversion.entities.UserKey;
import co.mizrahi.currency.conversion.repositories.UserKeyRepository;
import co.mizrahi.currency.conversion.services.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@RestController("/public")
@RequiredArgsConstructor
public class RegistrationController {

    public static final String PUBLIC_REGISTER = "/register";

    private final RegistrationService registrationService;

    @PostMapping(PUBLIC_REGISTER + "/{email}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> register(@PathVariable String email) {
        return this.registrationService.register(email);
    }
}
