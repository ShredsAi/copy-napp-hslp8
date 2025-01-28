package ai.shreds.application.exceptions;

import java.util.List;

public class ApplicationValidationException extends RuntimeException {

    private final List<String> errors;

    public ApplicationValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
