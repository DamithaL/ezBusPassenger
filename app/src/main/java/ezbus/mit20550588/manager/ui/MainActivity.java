package ezbus.mit20550588.manager.ui;

import static ezbus.mit20550588.manager.util.Constants.Log;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ezbus.mit20550588.manager.R;
import ezbus.mit20550588.manager.data.viewModel.BusViewModel;
import ezbus.mit20550588.manager.ui.Login.FleetRegistration;
import ezbus.mit20550588.manager.ui.Login.Login;
import ezbus.mit20550588.manager.ui.Settings.Settings;
import ezbus.mit20550588.manager.util.Constants;
import ezbus.mit20550588.manager.util.FleetStateManager;
import ezbus.mit20550588.manager.util.UserStateManager;
import yuku.ambilwarna.AmbilWarnaDialog;


public class MainActivity extends AppCompatActivity {

    // ------------------------------- DECLARING VARIABLES ------------------------------- //

    // -------------- widgets -------------- //


    // -------------- constants -------------- //

    private static final int ERROR_DIALOG_REQUEST = 9001;

    // -------------- variables -------------- //

    // ---- permissions ---- //
    private SharedPreferences preferences;

    private int currentColor = Color.BLACK;
    private TextView colorEditText;

    private List<String> validRouteNames = new ArrayList<>();

    private List<String> validRouteNumbers = new ArrayList<>();
    private AutoCompleteTextView routeNumberAutoCompleteTextView;
    private AutoCompleteTextView routeNameAutoCompleteTextView;


    private BusViewModel busViewModel;

    // ------------------------------- LIFECYCLE METHODS ------------------------------- //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        managerAuthentication();
        checkPermissions();
        busViewModel = new ViewModelProvider(this).get(BusViewModel.class);
        uiInitializations();

    }

    // Helper method to check if the entered text is a valid route
    private boolean isValidRoute(String enteredText, List<String> validRoutes) {
        for (String route : validRoutes) {
            if (route.equalsIgnoreCase(enteredText)) {
                return true;
            }
        }
        return false;
    }

//    private final TextWatcher filterTextWatcherForSearchBar = new TextWatcher() {
//        @Override
//        public void afterTextChanged(Editable s) {
//            boolean isTextEmpty = s.toString().equals("");
//            if (!isTextEmpty) {
//                busViewModel.fetchRouteNames();
//            }
//
//
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//          //  busViewModel.fetchRouteNames();
//          //  routeAutoCompleteTextView.showDropDown();
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//        }
//    };


    // ------------------------------- PERMISSION RELATED METHODS ------------------------------- //
    private void checkPermissions() {
        checkGoogleServicesAvailability();

    }

    public boolean checkGoogleServicesAvailability() {
        Log("checkGoogleServicesAvailability", "checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and the user can make map requests
            Log("checkGoogleServicesAvailability", "Google Play Services is working");

            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occurred but we can resolve it
            Log("checkGoogleServicesAvailability", "ERROR", "an error occurred but we can fix it");

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();

            Log("checkGoogleServicesAvailability", "ERROR", "an error occurred but we can fix it");
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
            Log("checkGoogleServicesAvailability", "ERROR", "You can't make map requests");

        }
        return false;
    }


    // ------------------------------- INITIALIZATION METHODS ------------------------------- //
    private void managerAuthentication() {
        Log("managerAuthentication", "starting");
        UserStateManager userManager = UserStateManager.getInstance();

        // Set up an OnPreDrawListener to the root view.
        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        // Check whether the initial data is ready.
//                        if (mViewModel.isReady()) {
                        // Check if the user is authenticated

                        if (userManager.isUserLoggedIn()) {
                            // User is authenticated, proceed to the main part of the app
                            // The content is ready. Start drawing.
                            content.getViewTreeObserver().removeOnPreDrawListener(this);
                            Log("managerAuthentication", "User logged in");
                            fleetAuthentication();
                            return true;
                        } else {
                            // User is not authenticated, start the authentication flow
                            Intent authIntent = new Intent(MainActivity.this, Login.class);
                            startActivity(authIntent);
                            // Finish the MainActivity to prevent it from being shown to the user
                            finish();
                            // Return false to suspend drawing until the authentication flow completes
                            Log("managerAuthentication", "User logged out");
                            return false;
                        }

                    }
                });
    }

    private void fleetAuthentication() {
        Log("fleetAuthentication", "starting");
        FleetStateManager fleetManager = FleetStateManager.getInstance();

        if (!fleetManager.isFleetLoggedIn()) {
            Intent authIntent = new Intent(MainActivity.this, FleetRegistration.class);
            startActivity(authIntent);
            finish();
        }
    }


    private void uiInitializations() {

        initSettingsButton();

        if (busViewModel.getBusCount == 0) {
            initNewBusForm();
        }


    }

    private void initNewBusForm() {

        // ------------ BUS COLOUR ------------ //
        colorEditText = findViewById(R.id.colorEditText);
        colorEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        // ------------ ADD BUS BUTTON ------------ //
        initNewBusFormSubmitButton();

        // ------------ BUS ROUTE NUMBER AND NAME ------------ //
        initLiveDataObservingForNewBusForm();


        // Set an OnFocusChangeListener to validate the input when focus changes
        routeAutoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            Log("MainActivity", "OnFocusChangeListener - hasFocus: " + hasFocus);

            TextInputLayout routeAutoCompleteTextViewInputLayout = findViewById(R.id.routeAutoCompleteTextViewInputLayout);

            if (!hasFocus) {
                // Check if the entered text is a valid option
                String enteredText = routeAutoCompleteTextView.getText().toString();
                if (!isValidRoute(enteredText, validRouteNames)) {
                    Log("MainActivity", "isValidRoute", "false");
                    routeAutoCompleteTextViewInputLayout.setEndIconVisible(false);
                    routeAutoCompleteTextView.setError("Invalid route number. Please select from the list.");
                } else {
                    Log("MainActivity", "isValidRoute", "true");
                    routeAutoCompleteTextView.setError(null); // Clear any previous error
                }
            }
            // If the AutoCompleteTextView is focused, show the dropdown list
            else {
                routeAutoCompleteTextView.showDropDown();


            }
        });
    }

    private void initLiveDataObservingForNewBusForm() {

        busViewModel.getRouteNumbersLiveData().observe(this, routeNumbers -> {
            if (routeNumbers != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, routeNumbers);
                routeNumberAutoCompleteTextView.setAdapter(adapter);
                if (validRouteNumbers != null) {
                    validRouteNumbers = routeNumbers;
                }
            }
        });

        busViewModel.getRouteNamesLiveData().observe(this, routeNames -> {
            if (routeNames != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, routeNames);
                routeNameAutoCompleteTextView.setAdapter(adapter);
                    validRouteNames = routeNames;
            }
        });

        busViewModel.getErrorMessageLiveData().observe(this, errorMessage -> {
            if (errorMessage != null) {
                TextView errorTextView = findViewById(R.id.errorMessageTextView);
                errorTextView.setText(errorMessage);
            }
        });

        busViewModel.fetchRouteNumbers();
        busViewModel.fetchRouteNames();
    }

    private void initSettingsButton() {
        ImageView settingsButton = findViewById(R.id.SettingsButton);

        // Set an OnClickListener for the Settings Button
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the SettingsActivity when the fab is clicked
                Intent intent = new Intent(MainActivity.this, Settings.class);
                // Intent intent = new Intent(MainActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }
        });

        Log("initSettingsButton", "initialized");
    }

    private void initNewBusFormSubmitButton() {
        Button newBusFormSubmitButton = findViewById(R.id.NewBusFormSubmitButton);

        // Set an OnClickListener for the Settings Button
        newBusFormSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // Open the SettingsActivity when the fab is clicked
//                Intent intent = new Intent(MainActivity.this, Settings.class);
//                // Intent intent = new Intent(MainActivity.this, CheckoutActivity.class);
//                startActivity(intent);

                Log("Main", "initNewBusFormSubmitButton", "clicked");
                Log("Main", "initNewBusFormSubmitButton", routeAutoCompleteTextView.getText().toString());
            }
        });

        Log("initSettingsButton", "initialized");
    }


    // ------------------------------- UTILITY METHODS ------------------------------- //
    private void toggleItemVisibility(View view, boolean isVisible) {
        if (view != null) {
            view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            Log("toggleItemVisibility", view.toString(), String.valueOf(isVisible));
        }
    }

    private void allItemVisibilitySwitcher(Map<View, Boolean> visibilityMap) {
        for (Map.Entry<View, Boolean> entry : visibilityMap.entrySet()) {
            View view = entry.getKey();
            Boolean isVisible = entry.getValue();

            if (view != null && isVisible != null) {
                view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                // Log("toggleItemsVisibility", view.toString(), String.valueOf(isVisible));
            }
        }
    }

    private void hideKeyboard(View view) {

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, currentColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // Handle cancel action
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // Update the current color and set it to the EditText
                currentColor = color;
                colorEditText.setBackgroundColor(color);
            }
        });

        colorPicker.show();
    }
}


// TODO: 2023-12-07  MAKE FONT STYLE instead of using bold etc
