package ezbus.mit20550588.passenger.ui.Login;

import static ezbus.mit20550588.passenger.util.Constants.Log;
import static ezbus.mit20550588.passenger.util.Constants.AUTH_SUCCESS_DIALOG_DURATION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.data.viewModel.AuthResult;
import ezbus.mit20550588.passenger.data.viewModel.AuthViewModel;
import ezbus.mit20550588.passenger.ui.ForgotPassword.ForgotPassword;
import ezbus.mit20550588.passenger.ui.MainActivity;
import ezbus.mit20550588.passenger.util.UserStateManager;
import ezbus.mit20550588.passenger.util.Validator;

public class Login extends AppCompatActivity {


    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViewModel();
        initializeUI();
        setupClickListeners();
    }

    private void initializeViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observe authentication result
        authViewModel.getAuthResultLiveData().observe(this, authResult -> {
            if (authResult != null) {
                handleAuthResult(authResult);
            }
        });

        // Observe the error message LiveData
        authViewModel.getErrorMessageLiveData().observe(this, errorMessage -> {
            if (errorMessage != null) {
                // Update your UI to display the error message (e.g., show a Toast or update a TextView)
                TextView errorTextView = findViewById(R.id.errorMessageTextView);
                errorTextView.setText(errorMessage);
                //  showToast(ErrorResponse);
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

        // Change the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.login_status_bar_color));

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
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.colorPrimary)), 23, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
        TextInputEditText emailText = findViewById(R.id.editTextEmailAddress);
        TextInputEditText passwordText = findViewById(R.id.editTextPassword);
        TextInputLayout passwordTextInputLayout = findViewById(R.id.editTextPasswordInputLayout);

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        // Trigger login operation in ViewModel
        authViewModel.loginUser(email, password, emailText, passwordText, passwordTextInputLayout);

        Log("LoginSubmit", "Login button clicked");


    }

    private void handleAuthResult(AuthResult authResult) {
        if (authResult.getStatus() == AuthResult.Status.SUCCESS) {
            Log("handleAuthResult", "Login successful");

            // Update the user login status
            UserStateManager userManager = UserStateManager.getInstance();

            userManager.setUserLoggedIn(true);
            userManager.setUser(authResult.getUser());

            Log("Signed up","NEW USER", userManager.getUser().toString());

            showLoginSuccessDialog(authResult.getUser().getName());
            Log("handleAuthResult", "USER", authResult.getUser().toString());
            Log("handleAuthResult", "HASHED PASSWORD", authResult.getUser().getHashedPassword());

        } else {
            // Authentication failed, show an error message
            showToast(authResult.getErrorMessage().toString());
            Log("handleAuthResult", "Login failed with code: " + authResult.getErrorMessage());
        }
    }

    private void showLoginSuccessDialog(String name) {

        Dialog dialog = new Dialog(Login.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(R.layout.rounded_alert_dialog);

        TextView userNameText = dialog.findViewById(R.id.userNameText);
        userNameText.setText(name);

        // Get the root view of the activity
        ViewGroup rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        // Show a semi-transparent overlay
        View overlay = new View(Login.this);
        overlay.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        overlay.setBackgroundColor(Color.parseColor("#BF000000")); // #80 for 50% alpha

        // Add the overlay to the root view
        rootView.addView(overlay);

        dialog.show();

        // Dismiss the dialog after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                navigateToMainActivity();
            }
        }, AUTH_SUCCESS_DIALOG_DURATION); // 5000 milliseconds = 5 seconds


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