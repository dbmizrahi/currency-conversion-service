package co.mizrahi.currency.conversion.repositories;

import co.mizrahi.currency.conversion.entities.ApiRequestLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Repository
public interface ApiRequestLogRepository extends MongoRepository<ApiRequestLog, String> {
    Optional<ApiRequestLog> findByApiKeyAndRequestDate(String apiKey, LocalDate requestDate);
}
