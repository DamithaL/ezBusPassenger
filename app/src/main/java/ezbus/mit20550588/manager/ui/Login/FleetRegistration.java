package ezbus.mit20550588.manager.ui.Login;

import static ezbus.mit20550588.manager.util.Constants.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import ezbus.mit20550588.manager.R;
import ezbus.mit20550588.manager.data.model.FleetModel;
import ezbus.mit20550588.manager.data.model.UserModel;
import ezbus.mit20550588.manager.data.viewModel.AuthViewModel;
import ezbus.mit20550588.manager.data.viewModel.FleetViewModel;
import ezbus.mit20550588.manager.ui.MainActivity;
import ezbus.mit20550588.manager.ui.Settings.ContactUs;
import ezbus.mit20550588.manager.ui.Settings.PrivacyPolicyActivity;
import ezbus.mit20550588.manager.ui.Settings.Settings;
import ezbus.mit20550588.manager.ui.Settings.TermConditions;
import ezbus.mit20550588.manager.util.Constants;
import ezbus.mit20550588.manager.util.FleetStateManager;
import ezbus.mit20550588.manager.util.UserStateManager;

public class FleetRegistration extends AppCompatActivity {

    private FleetViewModel fleetViewModel;

    private FleetModel newFleet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fleet_registration);

        // Show the loading progress bar
        findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);

        // Hide the registration layout
        findViewById(R.id.fleetRegistrationLayout).setVisibility(View.GONE);

        // Hide the pending layout
        findViewById(R.id.PendingLayout).setVisibility(View.GONE);

        // Hide the approved layout
        findViewById(R.id.ApprovedLayout).setVisibility(View.GONE);

        // Hide the suspended layout
        findViewById(R.id.SuspendedLayout).setVisibility(View.GONE);

        initializeViewModel();
        checkFleetStatus();
        initSettingsButton();
    }

    private void checkFleetStatus() {

        Log("checkFleetStatus", "Started");

        FleetStateManager fleetManager = FleetStateManager.getInstance();
        Log("checkFleetStatus", "fleetManager", String.valueOf(fleetManager.isFleetLoggedIn()));

        if (fleetManager.getFleet() != null) {
            Log("checkFleetStatus", "fleetManager.getFleet()", "not null");

            String fleetStatus = fleetManager.getFleet().getFleetStatus();

            if (Objects.equals(fleetStatus, "Approved")) {

                // Show the approved layout
                findViewById(R.id.ApprovedLayout).setVisibility(View.VISIBLE);
                Log("checkFleetStatus", "fleetStatus", "Approved");
                initContinueButton();

            } else if (Objects.equals(fleetStatus, "Pending")) {

                // Show the pending layout
                findViewById(R.id.PendingLayout).setVisibility(View.VISIBLE);
                // Trigger check status operation in ViewModel
                fleetViewModel.checkFleetStatus(fleetManager.getFleet());
                Log("checkFleetStatus", "fleetStatus", "Pending");

            } else if (Objects.equals(fleetStatus, "Rejected")) {

                // Show the rejected layout
                findViewById(R.id.RejectedLayout).setVisibility(View.VISIBLE);
                Log("checkFleetStatus", "fleetStatus", "Rejected");

            } else if (Objects.equals(fleetStatus, "Suspended")) {

                // Show the suspended layout
                findViewById(R.id.SuspendedLayout).setVisibility(View.VISIBLE);
                Log("checkFleetStatus", "fleetStatus", "Suspended");
            }

            // Hide the loading progress bar
            findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);

        } else {
            Log("checkFleetStatus", "fleetManager.getFleet()", "Null");
            initRegistrationLayout();
        }

    }

    private void initSettingsButton() {
        ImageView settingsButton = findViewById(R.id.SettingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FleetRegistration.this, Settings.class);
                startActivity(intent);
            }
        });

        Constants.Log("initSettingsButton", "initialized");
    }

    private void initContinueButton() {
        findViewById(R.id.ContinueButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log("initializeViewModel","ContinueButton", "Clicked");

                FleetStateManager fleetManager = FleetStateManager.getInstance();
                fleetManager.setFleetLoggedIn(true);

                // Go to Main
                Intent intent = new Intent(FleetRegistration.this, MainActivity.class);
                startActivity(intent);
                finish();
                Log("initializeViewModel","ContinueButton", "Clicked");

            }
        });
    }

    private void initializeViewModel() {


        fleetViewModel = new ViewModelProvider(this).get(FleetViewModel.class);

        // Observe check fleet status result
        fleetViewModel.getCheckFleetStatusLiveData().observe(this, FleetStatus -> {
            if (FleetStatus != null) {

                // Hide the loading progress bar
                findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);

                // Hide the pending layout
                findViewById(R.id.PendingLayout).setVisibility(View.GONE);

                FleetStateManager fleetManager = FleetStateManager.getInstance();
                FleetModel fleet = fleetManager.getFleet();
                FleetModel newFleet = new FleetModel(fleet);

                if (FleetStatus.getResponseCode() == 208) {
                    newFleet.setFleetStatus("Approved");
                    findViewById(R.id.ApprovedLayout).setVisibility(View.VISIBLE);
                    Log("initializeViewModel","Fleet Status", "Approved");
                    initContinueButton();

                } else if (FleetStatus.getResponseCode() == 403) {
                    newFleet.setFleetStatus("Suspended");
                    findViewById(R.id.SuspendedLayout).setVisibility(View.VISIBLE);

                } else if (FleetStatus.getResponseCode() == 406) {
                    newFleet.setFleetStatus("Pending");
                    findViewById(R.id.PendingLayout).setVisibility(View.VISIBLE);

                } else {
                    newFleet.setFleetStatus("Rejected");
                    findViewById(R.id.RejectedLayout).setVisibility(View.VISIBLE);
                }

                // Update the user newFleet status
                fleetManager.setFleet(newFleet);

            }
        });

        // Observe authentication result
        fleetViewModel.getRegReqResponseLiveData().observe(this, message -> {
            if (message != null) {

                // Hide the loading progress bar
                findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);

                handleSignUpResult(message);
            }
        });

        // Observe the error message LiveData
        fleetViewModel.getErrorMessageLiveData().observe(this, errorMessage -> {
            if (errorMessage != null) {

                // Hide the loading progress bar
                findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);

                TextView errorTextView = findViewById(R.id.errorMessageTextView);
                errorTextView.setText(errorMessage);

            }
        });
    }

    private void initRegistrationLayout() {

        // Hide the loading progress bar
        findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);

        // Show the registration layout
        findViewById(R.id.fleetRegistrationLayout).setVisibility(View.VISIBLE);


        // Help Text
        TextView helpTextView = findViewById(R.id.helpText);

        // Create the SpannableString
        SpannableString spannableStringHelp = new SpannableString(getString(R.string.fleet_register_help));

        // Set a ClickableSpan for the "contact us" part
        ClickableSpan clickableSpanLogin = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        ContactUs.class);
                startActivity(intent);
            }
        };

        // Set the ClickableSpan for the specific range
        spannableStringHelp.setSpan(clickableSpanLogin, 60, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Terms of Service" part bold
        spannableStringHelp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 60, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Terms of Service" part a different color
        spannableStringHelp.setSpan(new ForegroundColorSpan(getColor(R.color.my_primary)), 60, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the SpannableString to the TextView
        helpTextView.setText(spannableStringHelp);

        // Make the TextView clickable
        helpTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        setupClickListeners();

    }

    private void setupClickListeners() {
        // Listening to the Signup Button
        findViewById(R.id.SubmitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Show the loading progress bar
                findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);

                Submit();
            }
        });
    }


    private void Submit() {
        TextInputEditText fleetNameText = findViewById(R.id.editTextFleetName);
        TextInputEditText fleetRegNumberText = findViewById(R.id.editTextRegistrationNumber);

        String fleetName = fleetNameText.getText().toString();
        String fleetRegNumber = fleetRegNumberText.getText().toString();

        newFleet = new FleetModel(fleetName, fleetRegNumber, "Pending");

        // Trigger login operation in ViewModel
        fleetViewModel.registerFleet(newFleet, fleetNameText, fleetRegNumberText);

        Log("Submit", "Submit button clicked");
        Log("Submitted", "newFleet", newFleet.toString());

    }


    private void handleSignUpResult(String message) {
        if (message != null) {
            Log("handleAuthResult", "Registration request sent", message);

            // Update the user newFleet status
            FleetStateManager fleetManager = FleetStateManager.getInstance();
            fleetManager.setFleet(newFleet);

            // Hide the registration layout
            findViewById(R.id.fleetRegistrationLayout).setVisibility(View.GONE);

            // Show the pending layout
            findViewById(R.id.PendingLayout).setVisibility(View.VISIBLE);
        }
    }


    private void showToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FleetRegistration.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

}