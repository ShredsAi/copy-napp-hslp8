package ai.shreds.infrastructure.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class InfrastructureDataAccessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final LocalDateTime timestamp;
    private final Map<String, Object> additionalDetails;

    public enum ErrorCode {
        DATABASE_ERROR("DB-001"),
        EXTERNAL_SERVICE_ERROR("EXT-001"),
        CONNECTION_ERROR("CONN-001"),
        VALIDATION_ERROR("VAL-001"),
        TIMEOUT_ERROR("TIME-001"),
        UNKNOWN_ERROR("UNK-001");

        private final String code;

        ErrorCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public InfrastructureDataAccessException(String message) {
        this(message, ErrorCode.UNKNOWN_ERROR, null);
    }

    public InfrastructureDataAccessException(String message, Throwable cause) {
        this(message, ErrorCode.UNKNOWN_ERROR, cause);
    }

    public InfrastructureDataAccessException(String message, ErrorCode errorCode) {
        this(message, errorCode, null);
    }

    public InfrastructureDataAccessException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
        this.additionalDetails = new HashMap<>();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getAdditionalDetails() {
        return new HashMap<>(additionalDetails);
    }

    public InfrastructureDataAccessException addDetail(String key, Object value) {
        this.additionalDetails.put(key, value);
        return this;
    }

    @Override
    public String getMessage() {
        return String.format("[%s] %s (occurred at %s)", 
                errorCode.getCode(), 
                super.getMessage(), 
                timestamp);
    }

    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder()
                .append(getMessage())
                .append("
Additional Details:");

        additionalDetails.forEach((key, value) -> 
                sb.append("
  ")
                  .append(key)
                  .append(": ")
                  .append(value));

        if (getCause() != null) {
            sb.append("
Caused by: ")
              .append(getCause().getMessage());
        }

        return sb.toString();
    }

    public static InfrastructureDataAccessException databaseError(String message) {
        return new InfrastructureDataAccessException(message, ErrorCode.DATABASE_ERROR);
    }

    public static InfrastructureDataAccessException externalServiceError(String message) {
        return new InfrastructureDataAccessException(message, ErrorCode.EXTERNAL_SERVICE_ERROR);
    }

    public static InfrastructureDataAccessException connectionError(String message) {
        return new InfrastructureDataAccessException(message, ErrorCode.CONNECTION_ERROR);
    }

    public static InfrastructureDataAccessException timeoutError(String message) {
        return new InfrastructureDataAccessException(message, ErrorCode.TIMEOUT_ERROR);
    }

    public static InfrastructureDataAccessException validationError(String message) {
        return new InfrastructureDataAccessException(message, ErrorCode.VALIDATION_ERROR);
    }
}
