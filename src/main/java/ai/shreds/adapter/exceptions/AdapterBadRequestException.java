package ai.shreds.adapter.exceptions;

public class AdapterBadRequestException extends RuntimeException {

    public AdapterBadRequestException() {
        super();
    }

    public AdapterBadRequestException(String message) {
        super(message);
    }

    public AdapterBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
