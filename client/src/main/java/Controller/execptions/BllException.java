package Controller.execptions;

public class BllException extends RuntimeException {
    private static final long serialVersionUID = 938139791740375120L;

    public BllException(String message) {
        super(message);
    }

    public BllException(String message, Throwable cause) {
        super(message, cause);
    }
}
