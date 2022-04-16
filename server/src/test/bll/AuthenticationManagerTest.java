package bll;

import bll.exceptions.BllException;
import bll.service.UserService;
import bll.validators.PasswordValidator;
import model.User;
import model.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationManagerTest {
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        passwordEncoder = new BCryptPasswordEncoder();
        authenticationManager = new AuthenticationManager(userService, passwordEncoder,
                new PasswordValidator());
    }

    @Test(expected = BllException.class)
    public void testEncodeShortPassword() {
        authenticationManager.encode("a");
    }

    @Test
    public void testEncodeCorrectPassword() {
        String encoded = authenticationManager.encode("abc");
        assertTrue("correct password", passwordEncoder.matches("abc", encoded));
    }

    @Test
    public void testValidateNotFoundUser() {
        final String userName = "user";

        when(userService.findByUserName(userName)).thenReturn(null);
        assertFalse("user not found", authenticationManager.validateUser(userName, "pass"));
    }

    @Test
    public void testValidate() {
        final String userName = "user";
        final String password = "pass";

        when(userService.findByUserName(userName)).thenReturn(new User(userName,
                authenticationManager.encode(password), new UserRole()));
        assertTrue("valid login", authenticationManager.validateUser(userName, password));
    }

    @Test
    public void testValidateInvalidPassword() {
        final String userName = "user";
        final String password = "pass";

        when(userService.findByUserName(userName)).thenReturn(new User(userName,
                authenticationManager.encode("test"), new UserRole()));
        assertFalse("invalid password", authenticationManager.validateUser(userName, password));
    }
}