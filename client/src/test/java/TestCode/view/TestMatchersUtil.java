package TestCode.view;

import model.User;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public final class TestMatchersUtil {
    private TestMatchersUtil() {
    }

    public static Matcher<User> userNamed(final String username, final String password, final String role) {
        return new TypeSafeMatcher<User>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("user should be" + username + " " + password + " " + role);
            }

            @Override
            protected boolean matchesSafely(User obj) {
                return obj.getPassword().equals(password) && obj.getUsername().equals(username)
                        && obj.getUserRole().getName().equals(role);
            }
        };
    }
}
