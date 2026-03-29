package automata.core;

public class AutomatonException extends Exception {
    public AutomatonException(String message) {
        super(message);
    }
    public AutomatonException(String message, Throwable cause) {
        super(message, cause);
    }
}
