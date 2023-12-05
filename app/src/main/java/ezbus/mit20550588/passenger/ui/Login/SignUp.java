package ezbus.mit20550588.passenger.ui.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import ezbus.mit20550588.passenger.ui.MainActivity;
import ezbus.mit20550588.passenger.ui.Settings.PrivacyPolicyActivity;
import ezbus.mit20550588.passenger.ui.Settings.TermConditions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUp extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // For Authentication methods with Server
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        // Instantiate retrofit interface
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        // Listening to the Login Button
        findViewById(R.id.SignUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignupSubmit();
            }
        });



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



        // Verify email address
//        Button SignUpButton = (Button) this.findViewById(R.id.SignUpButton);
//        SignUpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(),
//                        SignUpEmailVerification.class);
//                startActivity(intent);
//            }
//        });
    }

    private void SignupSubmit() {
        final EditText nameText = findViewById(R.id.editTextName);
        final EditText emailText = findViewById(R.id.editTextEmailAddress);
        final EditText passwordText = findViewById(R.id.editTextPassword);

        HashMap<String, String> map = new HashMap<>();

        map.put("name", nameText.getText().toString());
        map.put("email", emailText.getText().toString());
        map.put("password", passwordText.getText().toString());

        Call<Void> call = retrofitInterface.executeSignup(map);

        Log.d(TAG, "Submit button clicked");
        Log.d(TAG, "map: "+ map);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 201) {
                    Toast.makeText(SignUp.this, "Signed up successfully", Toast.LENGTH_LONG).show();
                } else if (response.code() == 208) {
                    Toast.makeText(SignUp.this, "Already registered", Toast.LENGTH_LONG).show();
                }
                else if (response.code() == 500) {
                    Toast.makeText(SignUp.this, "Error", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SignUp.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });



    }
}