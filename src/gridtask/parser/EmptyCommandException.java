//@@author A0098084U
package gridtask.parser;

public class EmptyCommandException extends Exception{
    public EmptyCommandException() {
        super();
    }

    public EmptyCommandException(String message) {
        super(message);
    }

    public EmptyCommandException(Throwable cause) {
        super(cause);
    }

    public EmptyCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
