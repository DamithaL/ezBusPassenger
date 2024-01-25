package ezbus.mit20550588.manager.ui.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import ezbus.mit20550588.manager.R;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Back button
        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the current activity
                finish();
            }
        });


        // Contact Us Text

        TextView textView = findViewById(R.id.textContactUs);

        // Create the SpannableString
        SpannableString spannableString = new SpannableString(getString(R.string.about_us_contact_us));

        // Set a ClickableSpan for the "Contact Us" part
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        ContactUs.class);
                startActivity(intent);
            }
        };

        // Set the ClickableSpan for the specific range
        spannableString.setSpan(clickableSpan, 65, 75, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Contact Us" part bold
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 65, 75, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Contact Us" part a different color
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.my_primary)), 65, 75, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the SpannableString to the TextView
        textView.setText(spannableString);

        // Make the TextView clickable
        textView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    }
}