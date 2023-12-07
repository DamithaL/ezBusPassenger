package ezbus.mit20550588.passenger.ui.Login;

import static androidx.constraintlayout.widget.Constraints.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.data.model.UserModel;
import ezbus.mit20550588.passenger.data.remote.ApiServiceAuthentication;
import ezbus.mit20550588.passenger.data.viewModel.AuthResult;
import ezbus.mit20550588.passenger.data.viewModel.AuthViewModel;
import ezbus.mit20550588.passenger.ui.ForgotPassword.ForgotPassword;
import ezbus.mit20550588.passenger.ui.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViewModel();
        initializeUI();
        setupClickListeners();

//        authViewModel = new ViewModelProvider(this, new ViewModelFactory(userRepository)).get(AuthViewModel.class);


    }

    private void initializeViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observe authentication result
        authViewModel.getAuthResultLiveData().observe(this, authResult -> {
            if (authResult != null) {
                handleAuthResult(authResult);
            }
        });
    }

    private void setupClickListeners() {
        // Listening to the Login Button
        findViewById(R.id.LoginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginSubmit();
            }
        });
    }

    private void initializeUI() {
        // EZBus Passenger app main text

        TextView textView0 = findViewById(R.id.main_app_name);
        String html = "<font color=Color.WHITE>EZBus</font> <font color=#74EEF5>Passenger</font>";
        textView0.setText(Html.fromHtml(html));

        // SignUp Text

        TextView textView = findViewById(R.id.signUpText);

        // Create the SpannableString
        SpannableString spannableString = new SpannableString("Don’t have an account? Sign Up");

        // Set a ClickableSpan for the "Sign up" part
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Handle the click event for "Sign up"
                //Toast.makeText(Login.this, "Sign up clicked", Toast.LENGTH_SHORT).show();
                // Add your logic for navigating to the sign-up activity or any other action
                Intent intent = new Intent(getApplicationContext(),
                        SignUp.class);
                startActivity(intent);
            }
        };

        // Set the ClickableSpan for the specific range
        spannableString.setSpan(clickableSpan, 23, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Sign up" part bold
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 23, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Sign up" part a different color
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.my_primary)), 23, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the SpannableString to the TextView
        textView.setText(spannableString);

        // Make the TextView clickable
        textView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());


        // forgot password
        TextView clickableTextViewForgetPassword = this.findViewById(R.id.forgotPasswordText);

        clickableTextViewForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Define the target activity
                Intent intent = new Intent(Login.this, ForgotPassword.class);

                // Add any data you want to pass to the new activity
                //  intent.putExtra("key", "value");

                // Start the new activity
                startActivity(intent);
            }
        });

    }

    private void LoginSubmit() {
        EditText emailText = findViewById(R.id.editTextEmailAddress);
        EditText passwordText = findViewById(R.id.editTextPassword);

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        // Trigger login operation in ViewModel
        authViewModel.loginUser(email, password);

//        authViewModel.loginUser(email, password).observe(this, userModel -> {
//            if (userModel != null) {
//                handleLoginSuccess(userModel.getName());
//            } else {
//                handleLoginFailure();
//            }
//        });


        Log.d(TAG, "Login button clicked");


    }


    private void handleAuthResult(AuthResult authResult) {
        if (authResult.getStatus() == AuthResult.Status.SUCCESS) {
            // Authentication successful
           // UserModel user = authResult.getUser();
            handleLoginSuccess(authResult);
        } else {
            // Authentication failed, show an error message
            String errorMessage = authResult.getErrorMessage();
            handleLoginFailure(authResult);
        }
    }

    private void handleLoginSuccess(AuthResult result) {
        showLoginSuccessDialog(result.getUser().getName());
        Log.d(TAG, "Login successful");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToMainActivity();
            }
        }, 3000);
    }

    private void showLoginSuccessDialog(String name) {
        // Create a Dialog object
        Dialog dialog = new Dialog(Login.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.rounded_alert_dialog);

        TextView titleTextView = dialog.findViewById(R.id.titleTextView);
        titleTextView.setText(getString(R.string.login_results_welcome_text_1) + " " + name);
//                    TextView messageTextView = dialog.findViewById(R.id.messageTextView);
//                    messageTextView.setText(result.getEmail());

        dialog.show();

        // Dismiss the dialog after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                navigateToMainActivity();
            }
        }, 3000); // 5000 milliseconds = 5 seconds


    }

    private void handleLoginFailure(AuthResult error) {
        showToast(error.getErrorMessage().toString());


        Log.d(TAG, "Login failed with code: " + error.getErrorMessage());
//        showToast(error.getErrorMessage().toString());

//        if (responseCode == 401) {
//            showToast("Invalid email or password");
//        } else {
//            showToast("Login failed with code: " + responseCode);
//        }
//        Log.d(TAG, "Login failed with code: " + responseCode);
    }

    private void handleNetworkFailure(String errorMessage) {
        showToast("Connection failed: " + errorMessage);
        Log.d(TAG, "Connection failed: " + errorMessage);
    }

    private void showToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}