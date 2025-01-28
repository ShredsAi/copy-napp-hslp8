package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityMenuRecord;
import ai.shreds.domain.value_objects.DomainValueMenuRecordStatus;
import java.util.List;
import java.util.Optional;

public interface DomainOutputPortMenuRecordRepository {

    /**
     * Saves or updates a menu record.
     * @param menuRecord The menu record to save
     * @return The saved menu record with updated fields (id, timestamps)
     * @throws DomainValidationException if there are constraint violations
     */
    DomainEntityMenuRecord save(DomainEntityMenuRecord menuRecord);

    /**
     * Finds a menu record by its ID.
     * @param menuRecordId The ID of the menu record to find
     * @return Optional containing the menu record if found, empty otherwise
     */
    Optional<DomainEntityMenuRecord> findById(String menuRecordId);

    /**
     * Deletes a menu record by its ID.
     * @param menuRecordId The ID of the menu record to delete
     * @throws DomainMenuRecordNotFoundException if the record doesn't exist
     */
    void deleteById(String menuRecordId);

    /**
     * Finds all menu records for a restaurant with the specified status.
     * @param restaurantId The restaurant ID to filter by
     * @param status The status to filter by
     * @param page The page number (0-based)
     * @param size The size of each page
     * @return List of matching menu records
     */
    List<DomainEntityMenuRecord> findByRestaurantIdAndStatus(
        String restaurantId,
        DomainValueMenuRecordStatus status,
        int page,
        int size
    );

    /**
     * Finds all menu records for a restaurant with the specified status.
     * @param restaurantId The restaurant ID to filter by
     * @param status The status to filter by
     * @return List of matching menu records
     */
    default List<DomainEntityMenuRecord> findByRestaurantIdAndStatus(
        String restaurantId,
        DomainValueMenuRecordStatus status
    ) {
        return findByRestaurantIdAndStatus(restaurantId, status, 0, Integer.MAX_VALUE);
    }

    /**
     * Checks if a menu record exists by its ID.
     * @param menuRecordId The ID to check
     * @return true if the record exists, false otherwise
     */
    boolean existsById(String menuRecordId);

    /**
     * Counts menu records for a restaurant with the specified status.
     * @param restaurantId The restaurant ID to filter by
     * @param status The status to filter by
     * @return The count of matching records
     */
    long countByRestaurantIdAndStatus(String restaurantId, DomainValueMenuRecordStatus status);
}
