package ai.shreds.domain.value_objects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import ai.shreds.domain.exceptions.DomainValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DomainValuePrice {

    private static final BigDecimal MAX_ALLOWED_PRICE = new BigDecimal("99999.99");
    private static final int DECIMAL_PLACES = 2;
    private final BigDecimal amount;

    public DomainValuePrice(BigDecimal amount) {
        // Normalize the amount to have exactly 2 decimal places
        this.amount = amount != null ? amount.setScale(DECIMAL_PLACES, RoundingMode.HALF_UP) : null;
        validate();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void validate() {
        List<String> errors = new ArrayList<>();
        
        if (amount == null) {
            errors.add("Price amount cannot be null.");
        } else {
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                errors.add("Price amount cannot be negative.");
            }
            
            if (amount.compareTo(MAX_ALLOWED_PRICE) > 0) {
                errors.add(String.format("Price amount cannot exceed %s.", MAX_ALLOWED_PRICE));
            }
            
            // Check if the original amount had more than 2 decimal places
            if (amount.scale() > DECIMAL_PLACES) {
                errors.add(String.format("Price amount cannot have more than %d decimal places.", DECIMAL_PLACES));
            }
        }
        
        if (!errors.isEmpty()) {
            throw new DomainValidationException("Price validation failed.", errors);
        }
    }

    public DomainValuePrice add(DomainValuePrice other) {
        if (other == null) {
            throw new DomainValidationException("Cannot add null price.", List.of("Other price cannot be null"));
        }
        return new DomainValuePrice(this.amount.add(other.amount));
    }

    public DomainValuePrice subtract(DomainValuePrice other) {
        if (other == null) {
            throw new DomainValidationException("Cannot subtract null price.", List.of("Other price cannot be null"));
        }
        return new DomainValuePrice(this.amount.subtract(other.amount));
    }

    public DomainValuePrice multiply(int quantity) {
        return new DomainValuePrice(this.amount.multiply(new BigDecimal(quantity)));
    }

    public boolean isGreaterThan(DomainValuePrice other) {
        if (other == null) {
            throw new DomainValidationException("Cannot compare with null price.", List.of("Other price cannot be null"));
        }
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(DomainValuePrice other) {
        if (other == null) {
            throw new DomainValidationException("Cannot compare with null price.", List.of("Other price cannot be null"));
        }
        return this.amount.compareTo(other.amount) < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainValuePrice that = (DomainValuePrice) o;
        return amount.compareTo(that.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    @Override
    public String toString() {
        return amount != null ? amount.toString() : "null";
    }

    public static DomainValuePrice zero() {
        return new DomainValuePrice(BigDecimal.ZERO);
    }

    public static DomainValuePrice of(BigDecimal amount) {
        return new DomainValuePrice(amount);
    }

    public static DomainValuePrice of(String amount) {
        try {
            return new DomainValuePrice(new BigDecimal(amount));
        } catch (NumberFormatException e) {
            throw new DomainValidationException("Invalid price format.", 
                List.of("Price must be a valid decimal number"));
        }
    }
}
