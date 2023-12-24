package ezbus.mit20550588.passenger.util;

import android.util.Log;

import ezbus.mit20550588.passenger.ui.MainActivity;

public class Constants {
    public static final String TAG = MainActivity.class.getSimpleName();

    // Log Method with all parameters
    public static void Log(String method, String action, String message) {
        Log.d(TAG, "[DEV LOGS] " + method + ": " + action + ": " + message);
    }

    // Log Method with only method and action parameters
    public static void Log(String method, String action) {
        Log.d(TAG, "[DEV LOGS] " + method + ": " + action);
    }

    public static final int LOCATION_UPDATE_INTERVAL = 3000;

    public static final int AUTH_SUCCESS_DIALOG_DURATION = 3000;

    public static final String BASE_URL = "http://10.0.2.2:3000/";
}
