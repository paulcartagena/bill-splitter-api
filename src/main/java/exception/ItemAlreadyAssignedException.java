package exception;

public class ItemAlreadyAssignedException extends RuntimeException {
    public ItemAlreadyAssignedException(String message) {
        super(message);
    }
}
