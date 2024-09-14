package co.mizrahi.currency.conversion.controllers;

import co.mizrahi.currency.conversion.models.CurrencyConversionResponse;
import co.mizrahi.currency.conversion.services.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExchangeRatesController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/exchange-rates")
    public ResponseEntity<CurrencyConversionResponse> convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount,
            @RequestHeader("X-Api-Key") String apiKey) {
        var conversionResponse = this.exchangeRateService.getConversionResponse(from, to, amount, apiKey);
        return ResponseEntity.ok(conversionResponse);
    }
}
