package Controller.execptions;

public class LoggedInException extends BllException {
    private static final long serialVersionUID = -7629174787623674517L;

    public LoggedInException(String message) {
        super(message);
    }
}
