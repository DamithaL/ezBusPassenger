package ezbus.mit20550588.passenger.ui.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

import ezbus.mit20550588.passenger.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        // Back button
        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the current activity
                finish();
            }
        });

        web =(WebView)findViewById(R.id.webView);
        web.loadUrl(getString(R.string.privacy_url));
    }
}