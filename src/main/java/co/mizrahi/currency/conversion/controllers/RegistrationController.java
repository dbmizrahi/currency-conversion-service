package co.mizrahi.currency.conversion.controllers;

import co.mizrahi.currency.conversion.services.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> register(@RequestParam String email) {
        return this.registrationService.register(email);
    }
}
