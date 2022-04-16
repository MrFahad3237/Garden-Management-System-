package bll;

import bll.service.UserService;
import bll.validators.Validator;
import model.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticationManager {

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private Validator<String> passwordValidator;

    public AuthenticationManager(UserService userService,
                                 PasswordEncoder passwordEncoder, Validator<String> passwordValidator) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
    }

    public String encode(String pass) {
        passwordValidator.validate(pass);
        return passwordEncoder.encode(pass);
    }

    public boolean validateUser(String userName, String password) {
        User user = userService.findByUserName(userName);
        if (user == null) {
            return false;
        }

        return passwordEncoder.matches(password, user.getPassword());
    }
}
