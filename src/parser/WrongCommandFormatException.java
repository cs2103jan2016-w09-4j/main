//@@author Khanh

package parser;

public class WrongCommandFormatException extends Exception {
    public WrongCommandFormatException() {
        super();
    }

    public WrongCommandFormatException(String message) {
        super(message);
    }

    public WrongCommandFormatException(Throwable cause) {
        super(cause);
    }

    public WrongCommandFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}
