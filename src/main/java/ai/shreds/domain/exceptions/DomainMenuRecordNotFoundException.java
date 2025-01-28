package ai.shreds.domain.exceptions;

/**
 * Exception thrown when a menu record cannot be found in the system.
 * This is a domain-specific exception that represents a business rule violation
 * where a requested menu record does not exist.
 */
public class DomainMenuRecordNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String menuRecordId;

    /**
     * Constructs a new exception with a formatted message including the menu record ID.
     * @param menuRecordId The ID of the menu record that was not found
     */
    public DomainMenuRecordNotFoundException(String menuRecordId) {
        super(String.format("Menu record not found with ID: %s", menuRecordId));
        this.menuRecordId = menuRecordId;
    }

    /**
     * Constructs a new exception with a custom message.
     * @param message The detail message
     * @param menuRecordId The ID of the menu record that was not found
     */
    public DomainMenuRecordNotFoundException(String message, String menuRecordId) {
        super(message);
        this.menuRecordId = menuRecordId;
    }

    /**
     * Constructs a new exception with a custom message and cause.
     * @param message The detail message
     * @param cause The cause of this exception
     * @param menuRecordId The ID of the menu record that was not found
     */
    public DomainMenuRecordNotFoundException(String message, Throwable cause, String menuRecordId) {
        super(message, cause);
        this.menuRecordId = menuRecordId;
    }

    /**
     * Gets the ID of the menu record that was not found.
     * @return The menu record ID
     */
    public String getMenuRecordId() {
        return menuRecordId;
    }

    /**
     * Creates a formatted error message for logging or display purposes.
     * @return A formatted error message containing all relevant information
     */
    public String getFormattedMessage() {
        return String.format("Menu record with ID '%s' could not be found. %s", 
            menuRecordId, getMessage());
    }

    @Override
    public String toString() {
        return String.format("DomainMenuRecordNotFoundException{menuRecordId='%s', message='%s'}", 
            menuRecordId, getMessage());
    }
}
