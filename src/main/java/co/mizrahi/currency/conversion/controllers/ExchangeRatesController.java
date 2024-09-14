package co.mizrahi.currency.conversion.controllers;

import co.mizrahi.currency.conversion.models.CurrencyConversionResponse;
import co.mizrahi.currency.conversion.services.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<CurrencyConversionResponse> convert(@RequestParam String from, @RequestParam String to, @RequestParam BigDecimal amount) {
        CurrencyConversionResponse conversionResponse = this.exchangeRateService.getConversionResponse(from, to, amount);
        return ResponseEntity.ok(conversionResponse);
    }
}
