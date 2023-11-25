package ezbus.mit20550588.passenger.ui;

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
import android.widget.Toast;

import ezbus.mit20550588.passenger.R;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // EZBus Passenger app main text

        TextView textView0 = findViewById(R.id.main_app_name);
        String html = "<font color=Color.WHITE>EZBus</font> <font color=#74EEF5>Passenger</font>";
        textView0.setText(Html.fromHtml(html));

        TextView textView = findViewById(R.id.signUpText);

        // Create the SpannableString
        SpannableString spannableString = new SpannableString("Don’t have an account? Sign Up");

        // Set a ClickableSpan for the "Sign up" part
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Handle the click event for "Sign up"
                Toast.makeText(Login.this, "Sign up clicked", Toast.LENGTH_SHORT).show();
                // Add your logic for navigating to the sign-up activity or any other action
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


        // go to Main Activity
        Button LoginButton = (Button) this.findViewById(R.id.LoginButton);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(intent);
            }
        });


    }
}