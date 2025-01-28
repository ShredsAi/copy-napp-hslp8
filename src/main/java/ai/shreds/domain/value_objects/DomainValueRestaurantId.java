package ai.shreds.domain.value_objects;

import ai.shreds.domain.exceptions.DomainValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class DomainValueRestaurantId {

    private final String value;

    private DomainValueRestaurantId(String value) {
        this.value = value;
        validate();
    }

    public static DomainValueRestaurantId of(String value) {
        return new DomainValueRestaurantId(value);
    }

    public static DomainValueRestaurantId fromUUID(UUID uuid) {
        if (uuid == null) {
            throw new DomainValidationException("Restaurant ID validation failed.", 
                List.of("UUID cannot be null"));
        }
        return new DomainValueRestaurantId(uuid.toString());
    }

    public static DomainValueRestaurantId generate() {
        return new DomainValueRestaurantId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    public UUID toUUID() {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new DomainValidationException("Invalid UUID format", 
                List.of(String.format("Value '%s' is not a valid UUID", value)));
        }
    }

    public void validate() {
        List<String> errors = new ArrayList<>();
        
        if (value == null || value.trim().isEmpty()) {
            errors.add("Restaurant ID cannot be null or empty.");
        } else {
            try {
                UUID.fromString(value);
            } catch (IllegalArgumentException e) {
                errors.add(String.format("Restaurant ID must be a valid UUID. Invalid value: '%s'", value));
            }
        }

        if (!errors.isEmpty()) {
            throw new DomainValidationException("Restaurant ID validation failed.", errors);
        }
    }

    public boolean isSameRestaurant(DomainValueRestaurantId other) {
        if (other == null) {
            return false;
        }
        return this.value.equals(other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainValueRestaurantId that = (DomainValueRestaurantId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
