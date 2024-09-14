package co.mizrahi.currency.conversion.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ApiRequestLog")
public class ApiRequestLog {

    @Id
    private String id;  // Unique ID
    private String apiKey;  // API key for the user
    private LocalDate requestDate;  // Date of the request
    private int requestCount;  // Number of requests made on that date
}
