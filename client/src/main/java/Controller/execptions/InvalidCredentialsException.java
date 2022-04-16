package Controller.execptions;

public class InvalidCredentialsException extends BllException {
    private static final long serialVersionUID = 1045333081228633429L;

    public InvalidCredentialsException(String message) {
        super(message);
    }

}
