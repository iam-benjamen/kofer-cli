package kofer.exception;

public class KoferException extends RuntimeException{
    public KoferException(String message) {
        super(message);
    }

    public KoferException(String message, Throwable cause) {
        super(message, cause);
    }
}
