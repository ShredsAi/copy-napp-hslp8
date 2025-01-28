package ai.shreds.domain.value_objects;

import ai.shreds.shared.dtos.SharedMenuRecordStatusEnum;
import ai.shreds.domain.exceptions.DomainValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DomainValueMenuRecordStatus {
    private final SharedMenuRecordStatusEnum status;

    private DomainValueMenuRecordStatus(SharedMenuRecordStatusEnum status) {
        this.status = status;
        validate();
    }

    public static DomainValueMenuRecordStatus of(SharedMenuRecordStatusEnum status) {
        return new DomainValueMenuRecordStatus(status);
    }

    public static DomainValueMenuRecordStatus active() {
        return new DomainValueMenuRecordStatus(SharedMenuRecordStatusEnum.ACTIVE);
    }

    public static DomainValueMenuRecordStatus inactive() {
        return new DomainValueMenuRecordStatus(SharedMenuRecordStatusEnum.INACTIVE);
    }

    public static DomainValueMenuRecordStatus fromString(String status) {
        try {
            return new DomainValueMenuRecordStatus(SharedMenuRecordStatusEnum.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new DomainValidationException("Invalid status value", 
                List.of(String.format("'%s' is not a valid menu record status", status)));
        }
    }

    public SharedMenuRecordStatusEnum getStatus() {
        return status;
    }

    public boolean isActive() {
        return SharedMenuRecordStatusEnum.ACTIVE.equals(status);
    }

    public boolean isInactive() {
        return SharedMenuRecordStatusEnum.INACTIVE.equals(status);
    }

    public void validate() {
        List<String> errors = new ArrayList<>();
        if (status == null) {
            errors.add("Menu record status cannot be null.");
        }

        // Additional validation rules can be added here
        // For example, checking if the status transition is allowed
        
        if (!errors.isEmpty()) {
            throw new DomainValidationException("Menu record status validation failed", errors);
        }
    }

    public DomainValueMenuRecordStatus toggle() {
        if (isActive()) {
            return inactive();
        } else {
            return active();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainValueMenuRecordStatus that = (DomainValueMenuRecordStatus) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return status != null ? status.name() : "null";
    }
}
