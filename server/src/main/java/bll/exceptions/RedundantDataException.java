package bll.exceptions;

public class RedundantDataException extends BllException {
    private static final long serialVersionUID = 5342394559418748923L;

    public RedundantDataException(String message) {
        super(message);
    }
}
