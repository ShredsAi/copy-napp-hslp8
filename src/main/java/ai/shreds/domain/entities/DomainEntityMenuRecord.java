package ai.shreds.domain.entities;

import ai.shreds.domain.value_objects.DomainValueRestaurantId;
import ai.shreds.domain.value_objects.DomainValueMenuRecordStatus;
import ai.shreds.domain.exceptions.DomainValidationException;
import ai.shreds.shared.dtos.SharedMenuRecordResponse;
import ai.shreds.shared.dtos.SharedDishItemResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

public class DomainEntityMenuRecord {

    private final String id;
    private DomainValueRestaurantId restaurantId;
    private List<DomainEntityDishItem> dishItems;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private DomainValueMenuRecordStatus status;

    private DomainEntityMenuRecord(Builder builder) {
        this.id = builder.id;
        this.restaurantId = builder.restaurantId;
        this.dishItems = new ArrayList<>(builder.dishItems);
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.status = builder.status;
        validate();
    }

    public String getId() {
        return id;
    }

    public DomainValueRestaurantId getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(DomainValueRestaurantId restaurantId) {
        this.restaurantId = restaurantId;
        this.updatedAt = LocalDateTime.now();
    }

    public List<DomainEntityDishItem> getDishItems() {
        return new ArrayList<>(dishItems);
    }

    public void setDishItems(List<DomainEntityDishItem> dishItems) {
        this.dishItems = new ArrayList<>(dishItems);
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public DomainValueMenuRecordStatus getStatus() {
        return status;
    }

    public void setStatus(DomainValueMenuRecordStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void validate() {
        List<String> errors = new ArrayList<>();
        
        if (id == null || id.trim().isEmpty()) {
            errors.add("Menu record must have a valid ID.");
        }
        
        if (restaurantId == null) {
            errors.add("Menu record must have a valid restaurant ID.");
        } else {
            try {
                restaurantId.validate();
            } catch (DomainValidationException e) {
                errors.add("Invalid restaurant ID: " + e.getMessage());
            }
        }
        
        if (status == null) {
            errors.add("Menu record must have a valid status.");
        } else {
            try {
                status.validate();
            } catch (DomainValidationException e) {
                errors.add("Invalid status: " + e.getMessage());
            }
        }

        if (dishItems == null || dishItems.isEmpty()) {
            errors.add("Menu record must contain at least one dish item.");
        } else {
            for (DomainEntityDishItem item : dishItems) {
                try {
                    item.validate();
                } catch (DomainValidationException e) {
                    errors.add("Invalid dish item: " + e.getMessage());
                }
            }
        }

        if (createdAt == null) {
            errors.add("Menu record must have a creation timestamp.");
        }

        if (updatedAt == null) {
            errors.add("Menu record must have an update timestamp.");
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException("Menu record validation failed.", errors);
        }
    }

    public void validatePrices() {
        List<String> errors = new ArrayList<>();
        for (DomainEntityDishItem item : dishItems) {
            try {
                item.validatePrice();
            } catch (DomainValidationException e) {
                errors.add(String.format("Invalid price for dish '%s': %s", item.getDishName(), e.getMessage()));
            }
        }
        if (!errors.isEmpty()) {
            throw new DomainValidationException("Price validation failed.", errors);
        }
    }

    public void validateStatus() {
        if (status != null) {
            status.validate();
        } else {
            throw new DomainValidationException("Status validation failed.", List.of("Status cannot be null"));
        }
    }

    public void addDishItem(DomainEntityDishItem item) {
        if (item == null) {
            throw new DomainValidationException("Cannot add null dish item.", List.of("Dish item cannot be null"));
        }
        item.validate();
        this.dishItems.add(item);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeDishItem(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new DomainValidationException("Cannot remove dish item.", List.of("Item ID cannot be null or empty"));
        }
        boolean removed = this.dishItems.removeIf(d -> d.getId().equals(itemId));
        if (removed) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void updateDishItem(DomainEntityDishItem updatedItem) {
        if (updatedItem == null || updatedItem.getId() == null) {
            throw new DomainValidationException("Cannot update dish item.", List.of("Updated item or its ID cannot be null"));
        }
        
        Optional<DomainEntityDishItem> existing = this.dishItems.stream()
            .filter(d -> d.getId().equals(updatedItem.getId()))
            .findFirst();
            
        if (existing.isPresent()) {
            updatedItem.validate();
            DomainEntityDishItem dish = existing.get();
            dish.setDishName(updatedItem.getDishName());
            dish.setPrice(updatedItem.getPrice());
            dish.setDescription(updatedItem.getDescription());
            dish.setAdditionalMetadata(updatedItem.getAdditionalMetadata());
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new DomainValidationException("Cannot update dish item.", 
                List.of(String.format("Dish item with ID %s not found", updatedItem.getId())));
        }
    }

    public SharedMenuRecordResponse toSharedMenuRecordResponse() {
        SharedMenuRecordResponse response = new SharedMenuRecordResponse();
        response.setId(this.id);
        response.setRestaurantId(this.restaurantId != null ? this.restaurantId.getValue() : null);
        List<SharedDishItemResponse> items = new ArrayList<>();
        for (DomainEntityDishItem item : dishItems) {
            items.add(item.toSharedDishItemResponse());
        }
        response.setItems(items);
        response.setStatus(this.status != null ? this.status.getStatus().name() : null);
        response.setCreatedAt(this.createdAt != null ? this.createdAt.toString() : null);
        response.setUpdatedAt(this.updatedAt != null ? this.updatedAt.toString() : null);
        return response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainEntityMenuRecord that = (DomainEntityMenuRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DomainEntityMenuRecord{" +
                "id='" + id + '\'' +
                ", restaurantId=" + restaurantId +
                ", dishItems.size=" + (dishItems != null ? dishItems.size() : 0) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", status=" + status +
                '}';
    }

    public static class Builder {
        private String id;
        private DomainValueRestaurantId restaurantId;
        private List<DomainEntityDishItem> dishItems = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private DomainValueMenuRecordStatus status;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder restaurantId(DomainValueRestaurantId restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        public Builder dishItems(List<DomainEntityDishItem> dishItems) {
            this.dishItems = dishItems != null ? new ArrayList<>(dishItems) : new ArrayList<>();
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder status(DomainValueMenuRecordStatus status) {
            this.status = status;
            return this;
        }

        public DomainEntityMenuRecord build() {
            return new DomainEntityMenuRecord(this);
        }
    }
}
