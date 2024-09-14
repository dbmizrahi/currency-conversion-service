package co.mizrahi.currency.conversion.repositories;

import co.mizrahi.currency.conversion.entities.UserRequestLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Repository
public interface UserRequestLogRepository extends MongoRepository<UserRequestLog, String> {

    // Custom query to find all logs by username
    List<UserRequestLog> findByUsername(String username);

    // Custom query to find logs by username within a date range
    List<UserRequestLog> findByUsernameAndTimestampBetween(String username, LocalDateTime start, LocalDateTime end);
}
