package ezbus.mit20550588.passenger.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import ezbus.mit20550588.passenger.R;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Remove the title (text) from the action bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        // go to Privacy Policy Web page
        Button privacyPolicyButton = (Button) this.findViewById(R.id.privacyPolicyButton);
        privacyPolicyButton.setTextSize(18);
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
        Button termsConditionsButton = (Button) this.findViewById(R.id.termsOfServiceButton);
        termsConditionsButton.setTextSize(18);
        termsConditionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),
                        TermConditions.class);
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