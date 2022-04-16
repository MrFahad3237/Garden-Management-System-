package bll.exceptions;

public class ServerConnectionException extends BllException {
    private static final long serialVersionUID = 3389565085074074836L;

    public ServerConnectionException(String message) {
        super(message);
    }

    public ServerConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

}
