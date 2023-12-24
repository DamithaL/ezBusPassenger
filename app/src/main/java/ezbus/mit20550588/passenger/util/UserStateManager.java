package ezbus.mit20550588.passenger.util;

import android.content.Context;
import android.content.SharedPreferences;

public class UserStateManager {
    private static final String KEY_USER_LOGGED_IN = "user_logged_in";
    private static UserStateManager instance;
    private SharedPreferences preferences;

    public UserStateManager(Context context) {
        preferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
    }

    public static synchronized UserStateManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserStateManager(context);
        }
        return instance;
    }

    public boolean isUserLoggedIn() {
        return preferences.getBoolean(KEY_USER_LOGGED_IN, false);
    }

    public void setUserLoggedIn(boolean loggedIn) {
        preferences.edit().putBoolean(KEY_USER_LOGGED_IN, loggedIn).apply();
    }
}
