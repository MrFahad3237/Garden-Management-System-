package bll.validators;

import bll.exceptions.BllException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PasswordValidatorTest {
    private PasswordValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new PasswordValidator();
    }

    @Test(expected = BllException.class)
    public void testValidateShortPassword() {
        validator.validate("a");
    }

    @Test
    public void testValidate() {
        assertTrue("good password", validator.validate("abc"));
    }
}