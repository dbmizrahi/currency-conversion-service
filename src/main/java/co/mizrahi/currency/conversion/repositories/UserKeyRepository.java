package co.mizrahi.currency.conversion.repositories;

import co.mizrahi.currency.conversion.entities.UserKey;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Repository
public interface UserKeyRepository extends MongoRepository<UserKey, String> {
    UserKey findByApiKey(String apiKey);
}
