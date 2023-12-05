package ezbus.mit20550588.passenger.ui.Login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.ui.ForgotPassword.ForgotPassword;
import ezbus.mit20550588.passenger.ui.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";  //http://localhost:3000/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // For Authentication methods with Server
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        // Instantiate retrofit interface
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        // Listening to the Login Button
        findViewById(R.id.LoginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginSubmit();
            }
        });

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
        final EditText emailText = findViewById(R.id.editTextEmailAddress);
        final EditText passwordText = findViewById(R.id.editTextPassword);

        HashMap<String, String> map = new HashMap<>();

        map.put("email", emailText.getText().toString());
        map.put("password", passwordText.getText().toString());

        Call<LoginResult> call = retrofitInterface.executeLogin(map);

        Log.d(TAG, "Login button clicked");

        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.code() == 200) {

                    LoginResult result = response.body();
                    showLoginSuccessDialog(result.getName());
                    Log.d(TAG, response.code() + "Login successful");

                } else if (response.code() == 401) {
                    Log.d(TAG, response.code() + "Invalid email or password");
                    Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                }

                Log.d(TAG, response.code() + "error");
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Log.d(TAG, "Connection failed: " + t);
                Toast.makeText(Login.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

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

        // Dismiss the dialog after 5 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

                // Start your new activity here
                Intent intent = new Intent(Login.this, MainActivity.class);

                // Add any data you want to pass to the new activity
                //  intent.putExtra("key", "value");

                startActivity(intent);
                // For example, if you want to finish the current activity
                finish();
            }
        }, 3000); // 5000 milliseconds = 5 seconds

//                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Login.this);
//                    builder1.setTitle(result.getName());
//                    builder1.setMessage(result.getEmail());
//
//                    builder1.show();


    }
}