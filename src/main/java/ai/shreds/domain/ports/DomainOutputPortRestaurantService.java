package ai.shreds.domain.ports;

import ai.shreds.shared.dtos.SharedRestaurantLocation;
import ai.shreds.domain.value_objects.DomainValueRestaurantId;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface DomainOutputPortRestaurantService {

    /**
     * Validates if a restaurant reference exists and is valid.
     * @param restaurantId The restaurant ID to validate
     * @return true if the restaurant exists and is valid, false otherwise
     * @throws DomainValidationException if the restaurantId is null or invalid format
     */
    boolean validateRestaurantReference(DomainValueRestaurantId restaurantId);

    /**
     * Asynchronously validates a restaurant reference.
     * @param restaurantId The restaurant ID to validate
     * @return CompletableFuture containing the validation result
     */
    CompletableFuture<Boolean> validateRestaurantReferenceAsync(DomainValueRestaurantId restaurantId);

    /**
     * Retrieves location details for a restaurant.
     * @param restaurantId The restaurant ID to look up
     * @return Optional containing the restaurant location if found
     * @throws DomainValidationException if the restaurantId is null or invalid format
     */
    Optional<SharedRestaurantLocation> getRestaurantLocation(DomainValueRestaurantId restaurantId);

    /**
     * Asynchronously retrieves location details for a restaurant.
     * @param restaurantId The restaurant ID to look up
     * @return CompletableFuture containing Optional restaurant location
     */
    CompletableFuture<Optional<SharedRestaurantLocation>> getRestaurantLocationAsync(DomainValueRestaurantId restaurantId);

    /**
     * Validates multiple restaurant references in batch.
     * @param restaurantIds List of restaurant IDs to validate
     * @return Map of restaurant ID to validation result
     */
    java.util.Map<DomainValueRestaurantId, Boolean> validateRestaurantReferences(java.util.List<DomainValueRestaurantId> restaurantIds);

    /**
     * Retrieves location details for multiple restaurants in batch.
     * @param restaurantIds List of restaurant IDs to look up
     * @return Map of restaurant ID to Optional location details
     */
    java.util.Map<DomainValueRestaurantId, Optional<SharedRestaurantLocation>> getRestaurantLocations(
        java.util.List<DomainValueRestaurantId> restaurantIds);

    /**
     * Checks if the restaurant service is available.
     * @return true if the service is available, false otherwise
     */
    boolean isServiceAvailable();

    /**
     * Invalidates any cached data for a specific restaurant.
     * @param restaurantId The restaurant ID whose cache should be invalidated
     */
    void invalidateCache(DomainValueRestaurantId restaurantId);

    /**
     * Refreshes cached data for a specific restaurant.
     * @param restaurantId The restaurant ID whose cache should be refreshed
     * @return true if refresh was successful, false otherwise
     */
    boolean refreshCache(DomainValueRestaurantId restaurantId);
}
