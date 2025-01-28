package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityDishItem;
import java.util.List;
import java.util.Optional;

public interface DomainOutputPortDishItemRepository {

    /**
     * Saves or updates multiple dish items in a batch operation.
     * @param items The list of dish items to save
     * @return The list of saved dish items with updated IDs and other fields
     * @throws DomainValidationException if there are constraint violations
     */
    List<DomainEntityDishItem> saveAll(List<DomainEntityDishItem> items);

    /**
     * Deletes all dish items associated with a menu record.
     * @param menuRecordId The ID of the menu record whose items should be deleted
     * @throws DomainValidationException if the menuRecordId is invalid
     */
    void deleteByMenuRecordId(String menuRecordId);

    /**
     * Finds a specific dish item by its ID.
     * @param dishItemId The ID of the dish item to find
     * @return Optional containing the dish item if found, empty otherwise
     */
    Optional<DomainEntityDishItem> findById(String dishItemId);

    /**
     * Finds all dish items for a specific menu record.
     * @param menuRecordId The ID of the menu record
     * @return List of dish items associated with the menu record
     */
    List<DomainEntityDishItem> findByMenuRecordId(String menuRecordId);

    /**
     * Deletes a specific dish item by its ID.
     * @param dishItemId The ID of the dish item to delete
     * @throws DomainValidationException if the dishItemId is invalid
     */
    void deleteById(String dishItemId);

    /**
     * Saves or updates a single dish item.
     * @param item The dish item to save
     * @return The saved dish item with updated ID and other fields
     * @throws DomainValidationException if there are constraint violations
     */
    DomainEntityDishItem save(DomainEntityDishItem item);

    /**
     * Checks if a dish item exists by its ID.
     * @param dishItemId The ID to check
     * @return true if the dish item exists, false otherwise
     */
    boolean existsById(String dishItemId);

    /**
     * Counts the number of dish items associated with a menu record.
     * @param menuRecordId The ID of the menu record
     * @return The count of dish items
     */
    long countByMenuRecordId(String menuRecordId);

    /**
     * Updates multiple dish items in a batch operation.
     * @param items The list of dish items to update
     * @return The list of updated dish items
     * @throws DomainValidationException if any item is invalid or doesn't exist
     */
    List<DomainEntityDishItem> updateAll(List<DomainEntityDishItem> items);

    /**
     * Deletes multiple dish items by their IDs.
     * @param dishItemIds The list of dish item IDs to delete
     * @throws DomainValidationException if any ID is invalid
     */
    void deleteAllById(List<String> dishItemIds);
}
