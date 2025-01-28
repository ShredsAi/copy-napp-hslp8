package ai.shreds.domain.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Domain-specific exception for handling validation errors in the domain model.
 * This exception aggregates multiple validation errors and provides structured access to error details.
 */
public class DomainValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final List<String> errors;
    private final String field;

    /**
     * Constructs a new validation exception with a single error message.
     * @param message The error message
     */
    public DomainValidationException(String message) {
        this(message, new ArrayList<>(), null);
    }

    /**
     * Constructs a new validation exception with a message and list of specific errors.
     * @param message The general error message
     * @param errors List of specific validation errors
     */
    public DomainValidationException(String message, List<String> errors) {
        this(message, errors, null);
    }

    /**
     * Constructs a new validation exception with a message, errors, and the field that failed validation.
     * @param message The general error message
     * @param errors List of specific validation errors
     * @param field The field that failed validation
     */
    public DomainValidationException(String message, List<String> errors, String field) {
        super(message);
        this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
        this.field = field;
    }

    /**
     * Constructs a new validation exception with a message and cause.
     * @param message The error message
     * @param cause The cause of this exception
     */
    public DomainValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errors = Collections.emptyList();
        this.field = null;
    }

    /**
     * Gets the list of validation errors.
     * @return Unmodifiable list of validation error messages
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Gets the field that failed validation, if specified.
     * @return The field name or null if not specified
     */
    public String getField() {
        return field;
    }

    /**
     * Creates a formatted error message containing all validation errors.
     * @return A formatted string containing all error details
     */
    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder(getMessage());
        if (field != null) {
            sb.append(String.format(" Field: %s.", field));
        }
        if (!errors.isEmpty()) {
            sb.append(" Validation errors:
");
            errors.forEach(error -> sb.append("- ").append(error).append('
'));
        }
        return sb.toString();
    }

    /**
     * Creates a new builder for constructing validation exceptions.
     * @return A new ValidationExceptionBuilder
     */
    public static ValidationExceptionBuilder builder() {
        return new ValidationExceptionBuilder();
    }

    /**
     * Builder class for constructing DomainValidationException instances.
     */
    public static class ValidationExceptionBuilder {
        private String message;
        private List<String> errors = new ArrayList<>();
        private String field;

        public ValidationExceptionBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ValidationExceptionBuilder error(String error) {
            this.errors.add(error);
            return this;
        }

        public ValidationExceptionBuilder errors(List<String> errors) {
            this.errors.addAll(errors);
            return this;
        }

        public ValidationExceptionBuilder field(String field) {
            this.field = field;
            return this;
        }

        public DomainValidationException build() {
            return new DomainValidationException(message, errors, field);
        }
    }

    @Override
    public String toString() {
        return String.format("DomainValidationException{message='%s', field='%s', errors=%s}", 
            getMessage(), field, errors);
    }
}
