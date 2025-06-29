package kofer.exception;

/**
 * Exception thrown when data storage/retrieval operations fail.
 * This helps distinguish between data access issues and business logic errors.
 */
public class DataAccessException extends KoferException {
    
    private final String operation;
    
    public DataAccessException(String operation, String message) {
        super("Data access failed during " + operation + ": " + message);
        this.operation = operation;
    }
    
    public DataAccessException(String operation, String message, Throwable cause) {
        super("Data access failed during " + operation + ": " + message, cause);
        this.operation = operation;
    }
    
    public String getOperation() {
        return operation;
    }
}
