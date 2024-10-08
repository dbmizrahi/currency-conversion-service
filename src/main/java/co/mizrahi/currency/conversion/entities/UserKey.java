package co.mizrahi.currency.conversion.entities;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "UserKeys")
public class UserKey {

    @Id
    private String email;

    @Indexed(unique = true)
    private String apiKey;
}
