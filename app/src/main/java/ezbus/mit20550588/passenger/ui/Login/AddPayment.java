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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.ui.Settings.PrivacyPolicyActivity;
import ezbus.mit20550588.passenger.ui.Settings.TermConditions;

public class AddPayment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);


        // EZBus Passenger app main text
        TextView textView0 = findViewById(R.id.main_app_name);
        String html = "<font color=#025a66>EZBus</font> <font color=#0A969F>Passenger</font>";
        textView0.setText(Html.fromHtml(html));


        // Condition Text
        TextView conditionsTextView = findViewById(R.id.conditionsText);

        // Create the SpannableString
        SpannableString spannableString = new SpannableString("By clicking Save, you agree to our Terms of Service and Privacy Policy");

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
        spannableString.setSpan(clickableSpanTOS, 35, 51, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Terms of Service" part bold
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 35, 51, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Terms of Service" part a different color
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.colorPrimary)), 35, 51, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
        spannableString.setSpan(clickableSpanPP, 56, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Privacy Policy" part bold
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 56, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Privacy Policy" part a different color
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.colorPrimary)), 56, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the SpannableString to the TextView
        conditionsTextView.setText(spannableString);

        // Make the TextView clickable
        conditionsTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());


        // Skip Text
        TextView skipTextView = findViewById(R.id.skipText);

        // Create the SpannableString
        SpannableString spannableStringSkip = new SpannableString("Skip this for now");

        // Set a ClickableSpan for the "Skip" part
        ClickableSpan clickableSpanSkip = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        AccountSetupCompleted.class);
                startActivity(intent);
            }
        };

        // Set the ClickableSpan for the specific range
        spannableStringSkip.setSpan(clickableSpanSkip, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Skip" part bold
        spannableStringSkip.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Skip" part a different color
        spannableStringSkip.setSpan(new ForegroundColorSpan(getColor(R.color.colorPrimary)), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the SpannableString to the TextView
        skipTextView.setText(spannableStringSkip);

        // Make the TextView clickable
        skipTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());



        // Save payment method
        Button SaveButton = (Button) this.findViewById(R.id.SaveButton);
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        AccountSetupCompleted.class);
                startActivity(intent);
            }
        });
    }
}