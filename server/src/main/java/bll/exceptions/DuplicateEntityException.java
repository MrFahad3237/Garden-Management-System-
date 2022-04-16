package bll.exceptions;

public class DuplicateEntityException extends BllException {

    private static final long serialVersionUID = -7881844533148096941L;

    public DuplicateEntityException(String message) {
        super(message);
    }

}
