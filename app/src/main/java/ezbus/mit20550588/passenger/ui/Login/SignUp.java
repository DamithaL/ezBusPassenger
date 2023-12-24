package ezbus.mit20550588.passenger.ui.Login;

import static ezbus.mit20550588.passenger.util.Constants.Log;
import static ezbus.mit20550588.passenger.util.Constants.AUTH_SUCCESS_DIALOG_DURATION;

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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.data.network.ApiServiceAuthentication;
import ezbus.mit20550588.passenger.data.viewModel.AuthResult;
import ezbus.mit20550588.passenger.data.viewModel.AuthViewModel;
import ezbus.mit20550588.passenger.ui.MainActivity;
import ezbus.mit20550588.passenger.ui.Settings.PrivacyPolicyActivity;
import ezbus.mit20550588.passenger.ui.Settings.TermConditions;
import ezbus.mit20550588.passenger.util.UserStateManager;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUp extends AppCompatActivity {

    private AuthViewModel authViewModel;

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
                //  showToast(errorMessage);
            }
        });
    }

    private void initializeUI() {

        // EZBus Passenger app main text
        TextView textView0 = findViewById(R.id.main_app_name);
        String html = "<font color=#025a66>EZBus</font> <font color=#0A969F>Passenger</font>";
        textView0.setText(Html.fromHtml(html));


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
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.my_primary)), 38, 54, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.my_primary)), 59, 73, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
        spannableStringLogin.setSpan(new ForegroundColorSpan(getColor(R.color.my_primary)), 29, 34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

        // Trigger login operation in ViewModel
        authViewModel.registerUser(name, email, password,confirmPassword, nameText, emailText,passwordText, confirmPasswordText, passwordTextInputLayout, confirmPasswordTextInputLayout);

        Log("SignupSubmit", "SignUp button clicked");

    }


    private void handleAuthResult(AuthResult authResult) {
        if (authResult.getStatus() == AuthResult.Status.SUCCESS) {
            Log("handleAuthResult", "Login successful");

            // Update the user login status
            UserStateManager userManager = UserStateManager.getInstance(getApplicationContext());
            userManager.setUserLoggedIn(true);

            showLoginSuccessDialog(authResult.getUser().getName());

        } else {
            // Authentication failed, show an error message
            showToast(authResult.getErrorMessage().toString());
            Log("handleAuthResult", "Login failed with code: " + authResult.getErrorMessage());
        }
    }

    private void showLoginSuccessDialog(String name) {
        Dialog dialog = new Dialog(SignUp.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(R.layout.rounded_alert_dialog);

        TextView welcomeText = dialog.findViewById(R.id.welcomeText);
        welcomeText.setText(getString(R.string.signup_results_welcome_text_1));
        TextView messageText = dialog.findViewById(R.id.messageTextView);
        messageText.setText(getString(R.string.signup_results_welcome_text_2));
        TextView userNameText = dialog.findViewById(R.id.userNameText);
        userNameText.setText(name);

        // Get the root view of the activity
        ViewGroup rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        // Show a semi-transparent overlay
        View overlay = new View(SignUp.this);
        overlay.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        overlay.setBackgroundColor(Color.parseColor("#BF000000")); // #80 for 50% alpha

        // Add the overlay to the root view
        rootView.addView(overlay);

        dialog.show();

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
                Toast.makeText(SignUp.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignUp.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}