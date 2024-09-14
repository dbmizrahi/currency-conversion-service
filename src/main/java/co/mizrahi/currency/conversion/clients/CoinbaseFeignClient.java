package co.mizrahi.currency.conversion.clients;

import co.mizrahi.currency.conversion.models.ExchangeRates;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@FeignClient(name = "coinbaseClient", url = "https://api.coinbase.com/v2")
public interface CoinbaseFeignClient {

    @GetMapping("/exchange-rates")
    @Cacheable(value = "exchangeRates", key = "#currency")
    ExchangeRates getExchangeRates(
            @RequestHeader("Authorization") String authorizationToken,
            @RequestParam("currency") String currency
    );
}
