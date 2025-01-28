package ai.shreds.domain.entities;

import ai.shreds.domain.value_objects.DomainValuePrice;
import ai.shreds.domain.exceptions.DomainValidationException;
import ai.shreds.shared.dtos.SharedDishItemResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DomainEntityDishItem {

    private final String id;
    private String dishName;
    private DomainValuePrice price;
    private String description;
    private String additionalMetadata;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private DomainEntityDishItem(Builder builder) {
        this.id = builder.id;
        this.dishName = builder.dishName;
        this.price = builder.price;
        this.description = builder.description;
        this.additionalMetadata = builder.additionalMetadata;
        validate();
    }

    public String getId() {
        return id;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
        validate();
    }

    public DomainValuePrice getPrice() {
        return price;
    }

    public void setPrice(DomainValuePrice price) {
        this.price = price;
        validate();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdditionalMetadata() {
        return additionalMetadata;
    }

    public void setAdditionalMetadata(String additionalMetadata) {
        if (additionalMetadata != null && !additionalMetadata.trim().isEmpty()) {
            validateJsonFormat(additionalMetadata);
        }
        this.additionalMetadata = additionalMetadata;
    }

    public void validate() {
        List<String> errors = new ArrayList<>();

        if (id == null || id.trim().isEmpty()) {
            errors.add("Dish item must have a valid ID.");
        }

        if (dishName == null || dishName.trim().isEmpty()) {
            errors.add("Dish name cannot be null or empty.");
        } else if (dishName.length() > 255) {
            errors.add("Dish name cannot exceed 255 characters.");
        }

        if (price == null) {
            errors.add("Dish item must have a price.");
        } else {
            try {
                price.validate();
            } catch (DomainValidationException e) {
                errors.add("Invalid price: " + e.getMessage());
            }
        }

        if (description != null && description.length() > 1000) {
            errors.add("Description cannot exceed 1000 characters.");
        }

        if (additionalMetadata != null && !additionalMetadata.trim().isEmpty()) {
            try {
                validateJsonFormat(additionalMetadata);
            } catch (DomainValidationException e) {
                errors.add("Invalid additional metadata format: " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException("Dish item validation failed.", errors);
        }
    }

    public void validatePrice() {
        if (price == null) {
            throw new DomainValidationException("Price validation failed.", List.of("Price cannot be null"));
        }
        price.validate();
    }

    private void validateJsonFormat(String json) {
        try {
            objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new DomainValidationException("Invalid JSON format in additional metadata", 
                List.of("The provided additional metadata is not in valid JSON format"));
        }
    }

    public SharedDishItemResponse toSharedDishItemResponse() {
        SharedDishItemResponse response = new SharedDishItemResponse();
        response.setId(this.id);
        response.setDishName(this.dishName);
        response.setPrice(this.price != null ? this.price.getAmount() : null);
        response.setDescription(this.description);
        response.setAdditionalMetadata(this.additionalMetadata);
        return response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainEntityDishItem that = (DomainEntityDishItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DomainEntityDishItem{" +
                "id='" + id + '\'' +
                ", dishName='" + dishName + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", hasAdditionalMetadata=" + (additionalMetadata != null && !additionalMetadata.isEmpty()) +
                '}';
    }

    public static class Builder {
        private String id;
        private String dishName;
        private DomainValuePrice price;
        private String description;
        private String additionalMetadata;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder dishName(String dishName) {
            this.dishName = dishName;
            return this;
        }

        public Builder price(DomainValuePrice price) {
            this.price = price;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder additionalMetadata(String additionalMetadata) {
            this.additionalMetadata = additionalMetadata;
            return this;
        }

        public DomainEntityDishItem build() {
            return new DomainEntityDishItem(this);
        }
    }
}
