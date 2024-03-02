package ezbus.mit20550588.passenger.ui.Login;

import static java.security.AccessController.getContext;
import static ezbus.mit20550588.passenger.util.Constants.Log;
import static ezbus.mit20550588.passenger.util.Constants.AUTH_SUCCESS_DIALOG_DURATION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.data.model.UserModel;
import ezbus.mit20550588.passenger.data.network.ApiServiceAuthentication;
import ezbus.mit20550588.passenger.data.viewModel.AuthResult;
import ezbus.mit20550588.passenger.data.viewModel.AuthViewModel;
import ezbus.mit20550588.passenger.ui.MainActivity;
import ezbus.mit20550588.passenger.ui.PurchaseTicket.RedeemTicket;
import ezbus.mit20550588.passenger.ui.Settings.PrivacyPolicyActivity;
import ezbus.mit20550588.passenger.ui.Settings.TermConditions;
import ezbus.mit20550588.passenger.ui.virtualTicket;
import ezbus.mit20550588.passenger.util.UserStateManager;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUp extends AppCompatActivity {

    private AuthViewModel authViewModel;

    private UserModel newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeViewModel();
        initializeUI();
        setupClickListeners();

    }



    private void initializeViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observe authentication result
        authViewModel.getVerificationCodeLiveData().observe(this, verificationCode -> {
            if (verificationCode != null) {

                // Hide the loading progress bar
                findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);

                handleSignUpResult(verificationCode);
            }
        });

        // Observe the error message LiveData
        authViewModel.getErrorMessageLiveData().observe(this, errorMessage -> {
            if (errorMessage != null) {
                // Update your UI to display the error message (e.g., show a Toast or update a TextView)
                // Hide the loading progress bar
                findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);

                TextView errorTextView = findViewById(R.id.errorMessageTextView);
                errorTextView.setText(errorMessage);
                //  showToast(ErrorResponse);
            }
        });
    }

    private void initializeUI() {

        // Change the colors based on the current dark mode status
        RelativeLayout backgroundOverlay = findViewById(R.id.backgroundOverlay);
        TextView mainAppName = findViewById(R.id.main_app_name);
        TextView subAppName = findViewById(R.id.sub_app_name);

        // Get the current dark mode status
        Configuration configuration = getResources().getConfiguration();
        int currentNightModeStatus = configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightModeStatus == Configuration.UI_MODE_NIGHT_YES) {
            // Dark mode is active
            Log("initializeUI", "Dark mode is active");
            getWindow().setStatusBarColor(getColor(R.color.signup_status_bar_color_dark));
            backgroundOverlay.setBackgroundColor(getColor(R.color.black));
            mainAppName.setTextColor(getColor(R.color.colorOnPrimary));
            subAppName.setTextColor(getColor(R.color.white));

        } else {
            // Light mode is active
            Log("initializeUI", "Light mode is active");
            getWindow().setStatusBarColor(getColor(R.color.signup_status_bar_color_light));
            backgroundOverlay.setBackgroundColor(getColor(R.color.white));
            mainAppName.setTextColor(getColor(R.color.colorOnSecondary));
            subAppName.setTextColor(getColor(R.color.black));
        }


        // Hide the loading progress bar
        findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);

        // Condition Text
        TextView conditionsTextView = findViewById(R.id.conditionsText);

        // Create the SpannableString
        SpannableString spannableString = new SpannableString("By clicking Sign up, you agree to our Terms of Service and Privacy Policy");

        // Set a ClickableSpan for the "Terms of Service" part
        ClickableSpan clickableSpanTOS = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        TermConditions.class);
                startActivity(intent);
            }
        };

        // Set the ClickableSpan for the specific range
        spannableString.setSpan(clickableSpanTOS, 38, 54, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Terms of Service" part bold
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 38, 54, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Terms of Service" part a different color
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.colorPrimary)), 38, 54, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the SpannableString to the TextView
        conditionsTextView.setText(spannableString);

        // Make the TextView clickable
        conditionsTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());


        // Set a ClickableSpan for the "Privacy Policy" part
        ClickableSpan clickableSpanPP = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        };

        // Set the ClickableSpan for the specific range
        spannableString.setSpan(clickableSpanPP, 59, 73, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Privacy Policy" part bold
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 59, 73, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Privacy Policy" part a different color
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.colorPrimary)), 59, 73, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the SpannableString to the TextView
        conditionsTextView.setText(spannableString);

        // Make the TextView clickable
        conditionsTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());




        // Login Text
        TextView loginTextView = findViewById(R.id.loginText);

        // Create the SpannableString
        SpannableString spannableStringLogin = new SpannableString("You already have an account? Login");

        // Set a ClickableSpan for the "Login" part
        ClickableSpan clickableSpanLogin = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        Login.class);
                startActivity(intent);
            }
        };

        // Set the ClickableSpan for the specific range
        spannableStringLogin.setSpan(clickableSpanLogin, 29, 34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Terms of Service" part bold
        spannableStringLogin.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 29, 34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Terms of Service" part a different color
        spannableStringLogin.setSpan(new ForegroundColorSpan(getColor(R.color.colorPrimary)), 29, 34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the SpannableString to the TextView
        loginTextView.setText(spannableStringLogin);

        // Make the TextView clickable
        loginTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

    }

    private void setupClickListeners() {
        // Listening to the Signup Button
        findViewById(R.id.SignUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Show the loading progress bar
                findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);

                SignupSubmit();
            }
        });
    }



    private void SignupSubmit() {
        TextInputEditText nameText = findViewById(R.id.editTextName);
        TextInputEditText emailText = findViewById(R.id.editTextEmailAddress);
        TextInputEditText passwordText = findViewById(R.id.editTextPassword);
        TextInputEditText confirmPasswordText = findViewById(R.id.editTextConfirmPassword);

        TextInputLayout passwordTextInputLayout = findViewById(R.id.editTextPasswordInputLayout);
        TextInputLayout confirmPasswordTextInputLayout = findViewById(R.id.editTextConfirmPasswordInputLayout);

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String confirmPassword = confirmPasswordText.getText().toString();

        newUser = new UserModel(name, email, password, false);
        Log("Sign Up", "newUser", newUser.toString());


        // Trigger login operation in ViewModel
        authViewModel.registerUser(name, email, password,confirmPassword, nameText, emailText,passwordText, confirmPasswordText, passwordTextInputLayout, confirmPasswordTextInputLayout);

        Log("SignupSubmit", "SignUp button clicked");

    }


    private void handleSignUpResult(String verificationCode) {
        if (verificationCode != null) {
            Log("handleAuthResult", "Sign up successful");

            // Go to verification
            Intent intent = new Intent(SignUp.this, SignUpEmailVerification.class);
            intent.putExtra("verificationCode", verificationCode);
            intent.putExtra("user", newUser);

            Log("Sign up","handleAuthResult: verificationCode", verificationCode);
            Log("Sign up","handleAuthResult: newUser", newUser.toString());

            startActivity(intent);
            finish();
        } else {
            // Authentication failed, show an error message
            showToast("An issue has occurred. No verification code has been sent.");
            Log("handleAuthResult", "Sign up failed");
        }
    }


    private void showToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SignUp.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

}