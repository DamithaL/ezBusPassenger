package ezbus.mit20550588.passenger.util;

import android.util.Patterns;

public class Validator {
    public static boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    public static boolean isValidName(String name) {
        return !name.isEmpty();
    }

}
