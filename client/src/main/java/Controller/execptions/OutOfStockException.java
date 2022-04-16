package Controller.execptions;

public class OutOfStockException extends BllException {

    private static final long serialVersionUID = 438326124970166054L;

    public OutOfStockException(String message) {
        super(message);
    }
}
