package Controller.execptions;

public class AccessDeniedException extends BllException {
    private static final long serialVersionUID = -3695819992263934501L;

    public AccessDeniedException(String message) {
        super(message);
    }

}
