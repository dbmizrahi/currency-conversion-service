package co.mizrahi.currency.conversion.services;

import co.mizrahi.currency.conversion.entities.UserKey;
import co.mizrahi.currency.conversion.repositories.UserKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserKeyRepository userKeyRepository;

    @Override
    public ResponseEntity<String> register(String email) {
        if (userKeyRepository.findById(email).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Email is already registered!");
        }
        String apiKey = generateApiKey();
        UserKey userKey = UserKey.builder()
                .email(email)
                .apiKey(apiKey)
                .build();
        userKeyRepository.save(userKey);
        return ResponseEntity.created(URI.create("/public/register")).body(apiKey);
    }

    private String generateApiKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[24];  // 24 bytes = 192 bits
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);  // Generate base64 encoded string
    }
}
