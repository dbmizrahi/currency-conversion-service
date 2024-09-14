package co.mizrahi.currency.conversion.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Configuration
@RequiredArgsConstructor
public class CDPEnvironmentConfig {

    private final ConfigurableEnvironment environment;

    @PostConstruct
    public void loadJsonAsEnvironmentVariables() throws IOException {
        ClassPathResource resource = new ClassPathResource("cdp_api_key.json");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(resource.getInputStream());
        String name = jsonNode.get("name").asText();
        String privateKey = jsonNode.get("privateKey").asText();
        Map<String, Object> envVariables = new HashMap<>();
        envVariables.put("NAME", name);
        envVariables.put("PRIVATE_KEY", privateKey);
        environment.getPropertySources().addFirst(new MapPropertySource("jsonVariables", envVariables));
    }
}
