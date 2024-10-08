package ezbus.mit20550588.passenger.ui.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.button.MaterialButton;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.ui.Login.Login;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Back button
        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the current activity
                finish();
            }
        });

        // go to Profile Settings
        MaterialButton profileSettingsButton = findViewById(R.id.buttonProfileSettings);
        profileSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),
                        ProfileSettings.class);
                startActivity(intent);
            }
        });

        // go to Payment Settings
        MaterialButton paymentSettingsButton = findViewById(R.id.buttonPaymentSettings);
        paymentSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),
                        PaymentSettings.class);
                startActivity(intent);
            }
        });

        // go to Security Settings
        MaterialButton securitySettingsButton = findViewById(R.id.buttonSecuritySettings);
        securitySettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),
                        SecuritySettings.class);
                startActivity(intent);
            }
        });


        // go to About Us
        MaterialButton aboutUsButton = findViewById(R.id.buttonAboutUs);
        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),
                        AboutUs.class);
                startActivity(intent);
            }
        });


        // go to Contact Us
        MaterialButton contactUsButton = findViewById(R.id.buttonContactUs);
        contactUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),
                        ContactUs.class);
                startActivity(intent);
            }
        });

        // go to Privacy Policy Web page
        MaterialButton privacyPolicyButton = findViewById(R.id.privacyPolicyButton);
        privacyPolicyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),
                        PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });

        // go to Terms Conditions Web page
        Button termsConditionsButton = findViewById(R.id.terms_of_service_button);

        termsConditionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),
                        TermConditions.class);
                startActivity(intent);
            }
        });

        // Log out button clicked
        Button logOutButton = findViewById(R.id.logoutButton);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),
                        Login.class);
                startActivity(intent);
            }
        });

    }

    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}