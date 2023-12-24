package ezbus.mit20550588.passenger.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import ezbus.mit20550588.passenger.R;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 2000; // 2 seconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if the user is authenticated
                if (isUserAuthenticated()) {
                    // User is authenticated, proceed to the main part of the app
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(mainIntent);
                } else {
                    // User is not authenticated, start the authentication flow
                    Intent authIntent = new Intent(SplashScreen.this, Long.class);
                    startActivity(authIntent);
                }

                // Close the splash screen activity
                finish();
            }
        }, SPLASH_TIMEOUT);
    }

    private boolean isUserAuthenticated() {
        // Retrieve the authentication token from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String authToken = preferences.getString("authToken", "");

        // Check if the token exists and is not empty
        return !TextUtils.isEmpty(authToken);
    }
}