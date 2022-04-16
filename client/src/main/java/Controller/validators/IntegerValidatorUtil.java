package Controller.validators;

import Controller.execptions.BllException;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class IntegerValidatorUtil {
    static final Logger LOGGER = Logger.getLogger(IntegerValidatorUtil.class.getName());

    private IntegerValidatorUtil() {
    }

    public static boolean validateString(String s) {
        try {
            if (Integer.parseInt(s) <= 0) {
                throw new BllException("Number should be bigger than 0");
            }
            return true;
        } catch (NumberFormatException e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "IntegerValidatorUtil " + e.getMessage());
            }
            throw new BllException("Invalid Number", e);
        }
    }
}
