package co.mizrahi.currency.conversion.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created at 20/09/2024
 *
 * @author David Mizrahi
 */
@Data
@Component
@PropertySource(value = "classpath:cdp_api_key.json")
@ConfigurationProperties
public class CDPJsonProperties {
    private String name;
    private String privateKey;
}
