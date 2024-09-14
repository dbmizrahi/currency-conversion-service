package co.mizrahi.currency.conversion.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created at 14/09/2024
 *
 * @author David Mizrahi
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${cache.ttl:5}")
    private Integer ttl;

    @Value("${cache.maximumSize:1000}")
    private int maximumSize;

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(ttl, TimeUnit.MINUTES)
                .maximumSize(maximumSize);
    }

    @Bean
    public Caffeine<Object, Object> coinbaseAuthCacheConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .maximumSize(1);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeineConfig, Caffeine<Object, Object> coinbaseAuthCacheConfig) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("exchangeRates", "coinbaseAuthCache");
        cacheManager.setCaffeine(caffeineConfig);
        cacheManager.registerCustomCache("coinbaseAuthCache", coinbaseAuthCacheConfig.build());
        return cacheManager;
    }
}
