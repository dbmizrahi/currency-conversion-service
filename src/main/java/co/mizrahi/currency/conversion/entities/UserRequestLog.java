package co.mizrahi.currency.conversion.entities;

import co.mizrahi.currency.conversion.models.CurrencyConversionResponse;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "UserRequestLog")
public class UserRequestLog {

    @Id
    private String id;
    private String username;
    private LocalDateTime timestamp;
    private CurrencyConversionResponse currencyConversionResponse;
}
