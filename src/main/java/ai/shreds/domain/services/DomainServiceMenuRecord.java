package ai.shreds.domain.services;

import ai.shreds.domain.ports.DomainInputPortMenuRecord;
import ai.shreds.domain.ports.DomainOutputPortMenuRecordRepository;
import ai.shreds.domain.ports.DomainOutputPortDishItemRepository;
import ai.shreds.domain.ports.DomainOutputPortRestaurantService;
import ai.shreds.domain.entities.DomainEntityMenuRecord;
import ai.shreds.domain.entities.DomainEntityDishItem;
import ai.shreds.domain.exceptions.DomainMenuRecordNotFoundException;
import ai.shreds.domain.exceptions.DomainValidationException;
import ai.shreds.domain.value_objects.DomainValueMenuRecordStatus;
import ai.shreds.domain.value_objects.DomainValueRestaurantId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DomainServiceMenuRecord implements DomainInputPortMenuRecord {

    private final DomainOutputPortMenuRecordRepository repository;
    private final DomainOutputPortDishItemRepository dishItemRepository;
    private final DomainOutputPortRestaurantService restaurantService;

    public DomainServiceMenuRecord(
            DomainOutputPortMenuRecordRepository repository,
            DomainOutputPortDishItemRepository dishItemRepository,
            DomainOutputPortRestaurantService restaurantService) {
        this.repository = repository;
        this.dishItemRepository = dishItemRepository;
        this.restaurantService = restaurantService;
    }

    @Override
    public DomainEntityMenuRecord createMenuRecord(DomainEntityMenuRecord menuRecord) {
        validateMenuRecord(menuRecord);
        validateRestaurantReference(menuRecord.getRestaurantId());
        
        // Save menu record first
        DomainEntityMenuRecord savedRecord = repository.save(menuRecord);
        
        // Save dish items
        if (!menuRecord.getDishItems().isEmpty()) {
            dishItemRepository.saveAll(menuRecord.getDishItems());
        }
        
        return savedRecord;
    }

    @Override
    public DomainEntityMenuRecord updateMenuRecord(String menuRecordId, DomainEntityMenuRecord menuRecord) {
        DomainEntityMenuRecord existing = findMenuRecordById(menuRecordId);
        
        // Validate the update
        validateMenuRecord(menuRecord);
        if (!existing.getRestaurantId().equals(menuRecord.getRestaurantId())) {
            validateRestaurantReference(menuRecord.getRestaurantId());
        }

        // Update basic fields
        existing.setStatus(menuRecord.getStatus());
        existing.setRestaurantId(menuRecord.getRestaurantId());
        
        // Handle dish items updates
        updateDishItems(existing, menuRecord.getDishItems());
        
        // Update timestamp
        existing.setUpdatedAt(LocalDateTime.now());
        
        // Save and return
        return repository.save(existing);
    }

    @Override
    public void deleteMenuRecord(String menuRecordId) {
        DomainEntityMenuRecord existing = findMenuRecordById(menuRecordId);
        
        // First set status to inactive
        existing.setStatus(DomainValueMenuRecordStatus.inactive());
        existing.setUpdatedAt(LocalDateTime.now());
        repository.save(existing);
        
        // Then delete dish items
        dishItemRepository.deleteByMenuRecordId(menuRecordId);
        
        // Finally delete the record
        repository.deleteById(menuRecordId);
    }

    @Override
    public DomainEntityMenuRecord findMenuRecordById(String menuRecordId) {
        return repository.findById(menuRecordId)
            .orElseThrow(() -> new DomainMenuRecordNotFoundException(
                String.format("Menu record not found with id: %s", menuRecordId)));
    }

    @Override
    public List<DomainEntityMenuRecord> findMenuRecordsByCriteria(String restaurantId, DomainValueMenuRecordStatus status) {
        if (restaurantId == null || restaurantId.trim().isEmpty()) {
            throw new DomainValidationException("Invalid search criteria", 
                List.of("Restaurant ID cannot be null or empty"));
        }
        return repository.findByRestaurantIdAndStatus(restaurantId, status);
    }

    private void validateMenuRecord(DomainEntityMenuRecord menuRecord) {
        List<String> errors = new ArrayList<>();
        
        if (menuRecord == null) {
            throw new DomainValidationException("Menu record validation failed", 
                List.of("Menu record cannot be null"));
        }

        try {
            menuRecord.validate();
        } catch (DomainValidationException e) {
            errors.addAll(e.getErrors());
        }

        // Validate dish items
        if (menuRecord.getDishItems() != null) {
            for (DomainEntityDishItem item : menuRecord.getDishItems()) {
                try {
                    item.validate();
                    item.validatePrice();
                } catch (DomainValidationException e) {
                    errors.add(String.format("Invalid dish item '%s': %s", 
                        item.getDishName(), e.getMessage()));
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException("Menu record validation failed", errors);
        }
    }

    private void validateRestaurantReference(DomainValueRestaurantId restaurantId) {
        if (!restaurantService.validateRestaurantReference(restaurantId)) {
            throw new DomainValidationException("Invalid restaurant reference", 
                List.of(String.format("Restaurant with ID %s does not exist", restaurantId.getValue())));
        }
    }

    private void updateDishItems(DomainEntityMenuRecord existing, List<DomainEntityDishItem> newItems) {
        // Get existing items as map for easy lookup
        Map<String, DomainEntityDishItem> existingItemsMap = existing.getDishItems().stream()
            .collect(Collectors.toMap(DomainEntityDishItem::getId, item -> item));
        
        // Get new items as map for easy lookup
        Map<String, DomainEntityDishItem> newItemsMap = newItems.stream()
            .collect(Collectors.toMap(DomainEntityDishItem::getId, item -> item));
        
        // Items to remove (in existing but not in new)
        List<String> itemsToRemove = existingItemsMap.keySet().stream()
            .filter(id -> !newItemsMap.containsKey(id))
            .collect(Collectors.toList());
        
        // Items to add (in new but not in existing)
        List<DomainEntityDishItem> itemsToAdd = newItems.stream()
            .filter(item -> !existingItemsMap.containsKey(item.getId()))
            .collect(Collectors.toList());
        
        // Items to update (in both)
        List<DomainEntityDishItem> itemsToUpdate = newItems.stream()
            .filter(item -> existingItemsMap.containsKey(item.getId()))
            .collect(Collectors.toList());
        
        // Process removals
        itemsToRemove.forEach(existing::removeDishItem);
        
        // Process additions
        itemsToAdd.forEach(existing::addDishItem);
        
        // Process updates
        itemsToUpdate.forEach(existing::updateDishItem);
    }
}
