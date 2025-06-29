package kofer.exception;

/**
 * Exception thrown when input validation fails.
 * This helps distinguish between validation errors (user can fix) 
 * and system errors (user cannot fix).
 */
public class ValidationException extends KoferException {
    
    private final String field;
    private final String value;
    private final String expectedFormat;
    
    public ValidationException(String field, String value, String expectedFormat) {
        super(String.format("Invalid %s: '%s'. Expected: %s", field, value, expectedFormat));
        this.field = field;
        this.value = value;
        this.expectedFormat = expectedFormat;
    }
    
    public ValidationException(String message) {
        super(message);
        this.field = null;
        this.value = null;
        this.expectedFormat = null;
    }
    
    public String getField() {
        return field;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getExpectedFormat() {
        return expectedFormat;
    }
    
    /**
     * Check if this validation error can be retried (user can provide new input)
     */
    public boolean isRetryable() {
        return field != null; // If we know the specific field, user can retry
    }
}
