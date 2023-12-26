package ezbus.mit20550588.passenger.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import ezbus.mit20550588.passenger.data.model.UserModel;

public class UserStateManager {
    private static final String KEY_USER_LOGGED_IN = "user_logged_in";
    private static final String KEY_USER_MODEL = "user_model";
    private UserModel user;
    private static UserStateManager instance;
    private SharedPreferences preferences;
    private Gson gson; // Gson library for JSON serialization/deserialization


    public UserStateManager(Context context) {
        preferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        gson = new Gson();
        loadUserModel(); // Load user model from SharedPreferences when the class is instantiated

    }

    public static synchronized UserStateManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UserStateManager is not initialized. Call init() first.");
        }
        return instance;
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new UserStateManager(context.getApplicationContext());
        }
    }

    public boolean isUserLoggedIn() {
        return preferences.getBoolean(KEY_USER_LOGGED_IN, false);
    }

    public void setUserLoggedIn(boolean loggedIn) {
        preferences.edit().putBoolean(KEY_USER_LOGGED_IN, loggedIn).apply();
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
        saveUserModel(); // Save user model to SharedPreferences when it's set
    }

    private void saveUserModel() {
        String userModelJson = gson.toJson(user);
        preferences.edit().putString(KEY_USER_MODEL, userModelJson).apply();
    }

    private void loadUserModel() {
        String userModelJson = preferences.getString(KEY_USER_MODEL, null);
        if (userModelJson != null) {
            Type type = new TypeToken<UserModel>() {}.getType();
            user = gson.fromJson(userModelJson, type);
        }
    }

}
