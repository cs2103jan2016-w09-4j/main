package parser;

public class InvalidCommandException extends Exception{
    public InvalidCommandException() {
        super();
    }

    public InvalidCommandException(String message) {
        super(message);
    }

    public InvalidCommandException(Throwable cause) {
        super(cause);
    }

    public InvalidCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
