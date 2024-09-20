package co.mizrahi.currency.conversion.controllers;

import co.mizrahi.currency.conversion.logging.WriteResponseToDB;
import co.mizrahi.currency.conversion.models.CurrencyConversionResponse;
import co.mizrahi.currency.conversion.services.ExchangeRateService;
import lombok.RequiredArgsConstructor;
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

    @WriteResponseToDB(loggerId = "userLogger")
    @GetMapping("/exchange-rates")
    public CurrencyConversionResponse convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount) {
        return this.exchangeRateService.getConversionResponse(from, to, amount);
    }
}
