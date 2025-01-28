package ai.shreds.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class InfrastructureConfigRestClient {

    @Value("${restaurant.service.base-url}")
    private String restaurantServiceBaseUrl;

    @Value("${restaurant.service.connect-timeout-ms:5000}")
    private int connectTimeout;

    @Value("${restaurant.service.read-timeout-ms:5000}")
    private int readTimeout;

    @Value("${restaurant.service.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${restaurant.service.retry.initial-interval-ms:1000}")
    private long retryInitialInterval;

    @Value("${restaurant.service.circuit-breaker.failure-rate-threshold:50}")
    private float failureRateThreshold;

    @Value("${restaurant.service.circuit-breaker.wait-duration-in-open-state-ms:60000}")
    private long waitDurationInOpenState;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .requestFactory(this::clientHttpRequestFactory)
                .errorHandler(new DefaultResponseErrorHandler())
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }

    @Bean
    public RestaurantServiceConfig restaurantServiceConfig() {
        RestaurantServiceConfig config = new RestaurantServiceConfig();
        config.setBaseUrl(restaurantServiceBaseUrl);
        config.setConnectTimeout(connectTimeout);
        config.setReadTimeout(readTimeout);
        return config;
    }

    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(Duration.ofMillis(waitDurationInOpenState))
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .build();
    }

    @Bean
    public RetryConfig retryConfig() {
        return RetryConfig.custom()
                .maxAttempts(maxRetryAttempts)
                .waitDuration(Duration.ofMillis(retryInitialInterval))
                .retryExceptions(Exception.class)
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        factory.setBufferRequestBody(false);
        return factory;
    }

    public static class RestaurantServiceConfig {
        private String baseUrl;
        private int connectTimeout;
        private int readTimeout;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public int getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }
    }
}
