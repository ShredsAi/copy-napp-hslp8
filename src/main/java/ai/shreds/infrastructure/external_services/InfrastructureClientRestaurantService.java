package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.ports.DomainOutputPortRestaurantService;
import ai.shreds.domain.value_objects.DomainValueRestaurantId;
import ai.shreds.infrastructure.exceptions.InfrastructureDataAccessException;
import ai.shreds.shared.dtos.SharedRestaurantLocation;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Optional;

@Component
public class InfrastructureClientRestaurantService implements DomainOutputPortRestaurantService {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructureClientRestaurantService.class);
    private static final String CIRCUIT_BREAKER_NAME = "restaurantService";
    private static final String RETRY_NAME = "restaurantServiceRetry";

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final Duration timeout;

    public InfrastructureClientRestaurantService(
            RestTemplate restTemplate,
            @Value("${restaurant.service.base-url}") String baseUrl,
            @Value("${restaurant.service.timeout:5000}") long timeoutMillis) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.timeout = Duration.ofMillis(timeoutMillis);
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "validateRestaurantReferenceFallback")
    @Retry(name = RETRY_NAME)
    public boolean validateRestaurantReference(DomainValueRestaurantId restaurantId) {
        try {
            logger.debug("Validating restaurant reference for ID: {}", restaurantId.getValue());
            String url = String.format("%s/restaurants/%s/validate", baseUrl, restaurantId.getValue());
            
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return Optional.ofNullable(response.getBody()).orElse(false);
            }
            
            logger.warn("Unexpected response status {} while validating restaurant {}", 
                    response.getStatusCode(), restaurantId.getValue());
            return false;
            
        } catch (HttpClientErrorException.NotFound ex) {
            logger.info("Restaurant not found for ID: {}", restaurantId.getValue());
            return false;
        } catch (ResourceAccessException ex) {
            logger.error("Timeout while validating restaurant {}", restaurantId.getValue(), ex);
            throw new InfrastructureDataAccessException("Restaurant service timeout", ex);
        } catch (RestClientException ex) {
            logger.error("Error validating restaurant {}", restaurantId.getValue(), ex);
            throw new InfrastructureDataAccessException("Error calling Restaurant Service", ex);
        }
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getRestaurantLocationFallback")
    @Retry(name = RETRY_NAME)
    public SharedRestaurantLocation getRestaurantLocation(DomainValueRestaurantId restaurantId) {
        try {
            logger.debug("Fetching location for restaurant ID: {}", restaurantId.getValue());
            String url = String.format("%s/restaurants/%s/location", baseUrl, restaurantId.getValue());
            
            ResponseEntity<SharedRestaurantLocation> response = 
                    restTemplate.getForEntity(url, SharedRestaurantLocation.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }
            
            logger.warn("Unexpected response status {} while fetching location for restaurant {}", 
                    response.getStatusCode(), restaurantId.getValue());
            throw new InfrastructureDataAccessException("Unable to fetch restaurant location");
            
        } catch (HttpClientErrorException.NotFound ex) {
            logger.info("Restaurant location not found for ID: {}", restaurantId.getValue());
            return null;
        } catch (ResourceAccessException ex) {
            logger.error("Timeout while fetching location for restaurant {}", restaurantId.getValue(), ex);
            throw new InfrastructureDataAccessException("Restaurant service timeout", ex);
        } catch (RestClientException ex) {
            logger.error("Error fetching location for restaurant {}", restaurantId.getValue(), ex);
            throw new InfrastructureDataAccessException("Error calling Restaurant Service", ex);
        }
    }

    private boolean validateRestaurantReferenceFallback(DomainValueRestaurantId restaurantId, Exception ex) {
        logger.warn("Circuit breaker fallback: Unable to validate restaurant {}", restaurantId.getValue(), ex);
        throw new InfrastructureDataAccessException("Restaurant service is currently unavailable", ex);
    }

    private SharedRestaurantLocation getRestaurantLocationFallback(DomainValueRestaurantId restaurantId, Exception ex) {
        logger.warn("Circuit breaker fallback: Unable to fetch location for restaurant {}", 
                restaurantId.getValue(), ex);
        throw new InfrastructureDataAccessException("Restaurant service is currently unavailable", ex);
    }
}
