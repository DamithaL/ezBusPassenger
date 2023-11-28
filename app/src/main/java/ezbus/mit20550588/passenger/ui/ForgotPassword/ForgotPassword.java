package ezbus.mit20550588.passenger.ui.ForgotPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.ui.Login.Login;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // EZBus Passenger app main text

        TextView textView0 = findViewById(R.id.main_app_name);
        String html = "<font color=#025a66>EZBus</font> <font color=#0A969F>Passenger</font>";
        textView0.setText(Html.fromHtml(html));


        // Login Text

        TextView textView = findViewById(R.id.loginText);

        // Create the SpannableString
        SpannableString spannableString = new SpannableString("Back to Login");

        // Set a ClickableSpan for the "Login" part
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Handle the click event for "Login"

                // Add your logic for navigating to the sign-up activity or any other action
                Intent intent = new Intent(getApplicationContext(),
                        Login.class);
                startActivity(intent);
            }
        };

        // Set the ClickableSpan for the specific range
        spannableString.setSpan(clickableSpan, 8, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Login" part bold
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 8, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Login" part a different color
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.my_primary)), 8, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the SpannableString to the TextView
        textView.setText(spannableString);

        // Make the TextView clickable
        textView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());




        // send code button
        Button SendCodeButton = (Button) this.findViewById(R.id.SendCodeButton);
        SendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        ForgotPasswordCodeCheck.class);
                startActivity(intent);
            }
        });
    }
}