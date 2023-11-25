package ezbus.mit20550588.passenger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class PrivacyPolicyActivity extends AppCompatActivity {

    WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        getSupportActionBar().hide();

        web =(WebView)findViewById(R.id.webView);
        web.loadUrl(getString(R.string.privacy_url));
    }
}