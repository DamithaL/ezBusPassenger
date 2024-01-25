package ezbus.mit20550588.manager.ui.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import ezbus.mit20550588.manager.R;

public class ContactUs extends AppCompatActivity {

    TextView call,feedback,send_your_message;
    ImageView facebook, instagram, youtube,share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // Back button
        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the current activity
                finish();
            }
        });

        // Button for calling
        CardView callButton = findViewById(R.id.CallButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = "tel:" + getString(R.string.contact_us_phone);
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
                startActivity(dialIntent);
            }
        });

        // Button for emailing
        CardView emailButton = findViewById(R.id.EmailButton);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode(getString(R.string.contact_us_email)) + "?subject=" +
                        Uri.encode("your email id ") + "&body=" + Uri.encode("");

                Uri uri = Uri.parse(uriText);
                intent.setData(uri);
                startActivity(Intent.createChooser(intent, "Send Email"));

            }
        });

        // Button for chatting
        CardView chatButton = findViewById(R.id.ChatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an instance of the QuickChatFragment
                QuickChatFragment quickChatFragment = new QuickChatFragment();

                // Add the fragment to the fragment container with a tag
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, quickChatFragment, "QuickChatFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Button for LinkedIn
        ImageView linkedinButton = findViewById(R.id.LinkedinButton);
        linkedinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                gotoUrl(getString(R.string.contact_us_linkedin));
            }
            private void gotoUrl(String s) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });

        // Button for sharing the app
        ImageView shareButton = findViewById(R.id.ShareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a simple share message
                String shareSubject = "EZBus Passener";
                String shareMessage = "Check out this awesome app!";

                // Create a share intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

                // Start the activity for sharing
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

    }
}