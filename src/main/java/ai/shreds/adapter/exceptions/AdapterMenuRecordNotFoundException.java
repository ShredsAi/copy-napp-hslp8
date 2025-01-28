package ai.shreds.adapter.exceptions;

public class AdapterMenuRecordNotFoundException extends RuntimeException {
    public AdapterMenuRecordNotFoundException() {
        super();
    }

    public AdapterMenuRecordNotFoundException(String message) {
        super(message);
    }

    public AdapterMenuRecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
