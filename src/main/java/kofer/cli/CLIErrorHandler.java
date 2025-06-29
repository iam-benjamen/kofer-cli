package kofer.cli;

import kofer.exception.KoferException;

import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized error handling for CLI operations.
 * Provides consistent error messages and logging while hiding technical details from users.
 */
public class CLIErrorHandler {
    
    private static final Logger logger = Logger.getLogger(CLIErrorHandler.class.getName());
    private final boolean debugMode;
    
    public CLIErrorHandler(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
    /**
     * Handle errors gracefully for CLI operations
     * @param operation Description of what was being attempted
     * @param e The exception that occurred
     * @return true if the operation should be retried, false if it should be aborted
     */
    public boolean handleError(String operation, Exception e) {
        logger.log(Level.WARNING, "Error during " + operation, e);
        
        if (e instanceof NumberFormatException) {
            System.err.println("Invalid number format. Please enter a valid decimal number.");
            return true; 
        } else if (e instanceof DateTimeParseException) {
            System.err.println("Invalid date format. Please use YYYY-MM-DD format (e.g., 2024-01-15).");
            return true; 
        } else if (e instanceof IllegalArgumentException) {
            System.err.println("Invalid input: " + e.getMessage());
            return true; 
        } else if (e instanceof KoferException) {
            System.err.println("Error: " + e.getMessage());
            return false; 
        } else {
            System.err.println("An unexpected error occurred during " + operation + ".");
            if (debugMode) {
                System.err.println("Debug info: " + e.getMessage());
                e.printStackTrace();
            } else {
                System.err.println("Run with --debug for more details.");
            }
            return false; 
        }
    }
    
    /**
     * Handle validation errors with specific user guidance
     */
    public void handleValidationError(String field, String value, String expectedFormat) {
        System.err.printf("Invalid %s: '%s'%n", field, value);
        System.err.printf("Expected format: %s%n", expectedFormat);
        logger.info(String.format("Validation failed for %s: %s", field, value));
    }
    
    /**
     * Handle missing required arguments
     */
    public void handleMissingArguments(String command, String usage) {
        System.err.println("Missing required arguments for command: " + command);
        System.err.println("Usage: " + usage);
        System.err.println("Use 'kofer help' for more information.");
    }
    
    /**
     * Handle file/data access errors
     */
    public void handleDataError(String operation, Exception e) {
        logger.log(Level.SEVERE, "Data access error during " + operation, e);
        System.err.println("Unable to access data for " + operation + ".");
        System.err.println("Please check your permissions and try again.");
        
        if (debugMode) {
            System.err.println("Technical details: " + e.getMessage());
        }
    }
    
    /**
     * Validate and parse amount with proper error handling
     */
    public Double parseAmount(String amountStr, String context) {
        try {
            double amount = Double.parseDouble(amountStr);
            if (Double.isNaN(amount) || Double.isInfinite(amount)) {
                throw new NumberFormatException("Amount cannot be NaN or infinite");
            }
            return amount;
        } catch (NumberFormatException e) {
            handleValidationError("amount", amountStr, "decimal number (e.g., 123.45, -50.00)");
            return null;
        }
    }
    
    /**
     * Safe string parsing with null/empty checks
     */
    public String parseString(String value, String fieldName, boolean required) {
        if (value == null || value.trim().isEmpty()) {
            if (required) {
                System.err.println("Error: " + fieldName + " is required and cannot be empty.");
                return null;
            }
            return "";
        }
        return value.trim();
    }
    
    /**
     * Create a user-friendly error summary
     */
    public void printErrorSummary(String operation, int errorCount) {
        if (errorCount > 0) {
            System.err.printf("%n%d error(s) occurred during %s.%n", errorCount, operation);
            System.err.println("Please correct the issues above and try again.");
        }
    }
}
