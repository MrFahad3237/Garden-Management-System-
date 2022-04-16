package bll.validators;

import bll.exceptions.BllException;

public class PasswordValidator implements Validator<String> {

    @Override
    public boolean validate(String pass) {
        if (pass.length() < 3) {
            throw new BllException("Too short password! (minimum 3 characters)");
        }
        return true;
    }
}
