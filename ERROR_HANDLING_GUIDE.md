# Error Handling Best Practices for CLI Tools in Java

## Overview

This guide explains the error handling improvements made to the Kofer CLI tool and provides general best practices for handling errors in Java CLI applications.

## Key Principles for CLI Error Handling

### 1. **User-Friendly vs. Developer-Friendly**
- **Users** need clear, actionable error messages
- **Developers** need detailed technical information for debugging
- **Solution**: Separate user-facing messages from debug information

### 2. **Fail Fast, Fail Clearly**
- Validate inputs as early as possible
- Provide specific error messages about what went wrong
- Suggest how to fix the problem

### 3. **Graceful Degradation**
- Don't crash the entire application for recoverable errors
- Allow users to retry operations when possible
- Exit cleanly with appropriate exit codes

## Error Handling Architecture

### Exception Hierarchy
```
Exception
└── RuntimeException
    └── KoferException (base domain exception)
        ├── ValidationException (user input errors)
        └── DataAccessException (storage/retrieval errors)
```

### Error Handler Pattern
The `CLIErrorHandler` class centralizes error handling logic:
- Consistent error message formatting
- Debug mode support
- Logging for troubleshooting
- Retry logic for recoverable errors

## Implementation Examples

### 1. Input Validation
```java
// Before (problematic)
double amount = Double.parseDouble(args[2]); // Can throw NumberFormatException

// After (improved)
Double amount = errorHandler.parseAmount(args[2], "transaction amount");
if (amount == null) {
    throw new ValidationException("amount", args[2], "decimal number");
}
```

### 2. Error Classification
```java
try {
    // Business logic
} catch (ValidationException e) {
    // User can fix this - show helpful message
    System.err.println("Input error: " + e.getMessage());
} catch (DataAccessException e) {
    // System issue - log details, show generic message
    errorHandler.handleDataError("operation", e);
} catch (KoferException e) {
    // Domain error - show business-friendly message
    System.err.println("Error: " + e.getMessage());
}
```

### 3. Debug Mode Support
```bash
# Normal mode - user-friendly messages
kofer add transaction invalid_amount grocery

# Debug mode - technical details
kofer --debug add transaction invalid_amount grocery
```

## Best Practices Applied

### ✅ Do's

1. **Specific Exception Types**
   - Use `ValidationException` for user input errors
   - Use `DataAccessException` for storage issues
   - Use domain-specific exceptions for business logic errors

2. **Meaningful Error Messages**
   ```java
   // Good
   throw new ValidationException("amount", "abc", "decimal number (e.g., 123.45)");
   
   // Bad
   throw new Exception("Invalid input");
   ```

3. **Consistent Exit Codes**
   - 0: Success
   - 1: User error (validation, missing args)
   - 2: System error (file access, network)

4. **Logging for Debugging**
   ```java
   logger.log(Level.WARNING, "Error during " + operation, e);
   ```

5. **Input Sanitization**
   ```java
   String description = value.replaceAll("^\"|\"$", ""); // Remove quotes
   ```

### ❌ Don'ts

1. **Don't Catch Generic Exception**
   ```java
   // Bad
   catch (Exception e) {
       System.out.println("Something went wrong");
   }
   
   // Good
   catch (NumberFormatException e) {
       System.err.println("Invalid number format: " + e.getMessage());
   } catch (Exception e) {
       logger.log(Level.SEVERE, "Unexpected error", e);
       System.err.println("An unexpected error occurred");
   }
   ```

2. **Don't Expose Stack Traces to Users**
   ```java
   // Bad
   e.printStackTrace(); // Always visible
   
   // Good
   if (debugMode) {
       e.printStackTrace();
   }
   ```

3. **Don't Ignore Exceptions**
   ```java
   // Bad
   try {
       riskyOperation();
   } catch (Exception e) {
       // Silent failure
   }
   
   // Good
   try {
       riskyOperation();
   } catch (Exception e) {
       logger.log(Level.WARNING, "Operation failed", e);
       return false;
   }
   ```

## CLI-Specific Considerations

### 1. **Exit Codes Matter**
CLI tools should exit with appropriate codes:
```java
System.exit(0); // Success
System.exit(1); // User error
System.exit(2); // System error
```

### 2. **Help on Error**
Always guide users to help:
```java
System.err.println("Use 'kofer help' for usage information.");
```

### 3. **Validate Early**
Check arguments before doing any work:
```java
if (args.length < 4) {
    errorHandler.handleMissingArguments("add transaction", usage);
    return;
}
```

### 4. **Progress Feedback**
For successful operations, confirm what happened:
```java
System.out.println("✓ Transaction added successfully!");
System.out.printf("  Added $%.2f to category '%s'%n", amount, category);
```

## Testing Error Handling

### Unit Tests
```java
@Test
void shouldThrowValidationExceptionForInvalidAmount() {
    assertThrows(ValidationException.class, () -> {
        cli.processCommand(new String[]{"add", "transaction", "invalid", "grocery"});
    });
}
```

### Integration Tests
```bash
# Test error scenarios
./kofer add transaction                    # Missing args
./kofer add transaction abc grocery        # Invalid amount
./kofer add transaction 50.00             # Missing category
```

## Monitoring and Logging

### Log Levels
- `SEVERE`: System errors, data corruption
- `WARNING`: Recoverable errors, validation failures
- `INFO`: Normal operations, user actions
- `FINE`: Debug information

### What to Log
```java
// Log user actions (for audit)
logger.info("User added transaction: " + transaction);

// Log errors (for debugging)
logger.log(Level.WARNING, "Validation failed for amount: " + input, e);

// Log system events (for monitoring)
logger.info("Data file loaded successfully");
```

## Summary

Good error handling in CLI tools requires:
1. **Clear separation** between user and developer information
2. **Specific exception types** for different error categories
3. **Consistent messaging** and exit codes
4. **Debug mode** for technical details
5. **Proper logging** for troubleshooting
6. **Input validation** as early as possible
7. **Graceful failure** with helpful guidance

This approach makes your CLI tool more professional, easier to debug, and more user-friendly.
