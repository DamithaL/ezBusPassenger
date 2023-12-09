package ezbus.mit20550588.passenger.ui;

import static ezbus.mit20550588.passenger.util.Constants.Log;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ezbus.mit20550588.passenger.data.model.RecentSearchModel;
import ezbus.mit20550588.passenger.data.viewModel.RecentSearchViewModel;
import ezbus.mit20550588.passenger.ui.adapters.PlacesAutoCompleteAdapter;
import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.ui.Settings.Settings;
import ezbus.mit20550588.passenger.ui.adapters.RecentSearchAdapter;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PlacesAutoCompleteAdapter.ClickListener {

    // ------------------------------- DECLARING VARIABLES ------------------------------- //
    // widgets
    private GoogleMap myMap;

    // constants
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    // variables
    private Boolean mLocationPermissionsGranted = false;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private RecyclerView recyclerViewForAutocomplete;
    private RelativeLayout recentSearchLayout;
    private LatLng SourceLocationLatLng = new LatLng(6.9022, 79.8612);
    private LatLng DestinationLocationLatLng = new LatLng(6.9271, 79.8612);
    private String SourceLocationName = "Origin placeholder";
    private String DestinationLocationName = "Destination placeholder";

    private Polyline directionRoutePolyline;
    private RecentSearchViewModel recentSearchViewModel;

    private Button DirectionsButton;

    private Boolean locationMarked;

    // ------------------------------- LIFECYCLE METHODS ------------------------------- //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        checkPermissions();

        uiInitializations();

        // Assuming you are using a SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {


                // Set a click listener for the map
                myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(com.google.android.gms.maps.model.LatLng latLng) {
                        // Lose focus from the search bar
                        TextInputLayout placeSearchLayout = findViewById(R.id.place_search_layout);
                        placeSearchLayout.clearFocus();
                        hideKeyboard(findViewById(R.id.map));
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start any operations that should occur when the activity is visible.
        // Example: If your app tracks the user's location in the background, you might start a location tracking service in the onStart method to ensure that it runs when the activity becomes visible.
        Log("onStart", "MainActivity started");

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume any operations appropriate for when the activity is in the foreground.
        // Example: If your app is using real-time data, such as updating the user's location on the map, you might resume location updates or refresh the map in the onResume method.
        Log("onResume", "MainActivity resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause any operations appropriate for when the activity is going into the background.
        // Example: If your app is actively updating the user's location on the map, you might pause location updates or stop refreshing the map in the onPause method to conserve resources.

        Log("onPause", "MainActivity paused");


    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop any operations that should not continue when the activity is not visible.
        // Example: If your app is streaming audio or video, you might pause or stop the streaming process in the onStop method. This prevents unnecessary resource usage when the user is not actively using the app.

        Log("onStop", "MainActivity stopped");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release resources, unregister listeners, etc. before the activity is destroyed
        // Example: If your app is using any resources or services that need to be released explicitly, such as stopping a location tracking service, you might do so in the onDestroy method.
        Log("onDestroy", "MainActivity destroyed");
    }


    // ------------------------------- CALLBACK METHODS ------------------------------- //
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log("onMapReady", "map is ready");


        myMap = googleMap;

        ////////// PLACES API TASK //////////////  added according to YT CodeWithMitch

        if (mLocationPermissionsGranted) {

            Log("onMapReady", "checking for permissions and settings map controls");


            // Checking permissions
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log("onMapReady", "permissions are not granted");

                return;
            }

            // Map Settings
            myMap.setMyLocationEnabled(true);
            myMap.getUiSettings().setZoomControlsEnabled(true);
            //  myMap.getUiSettings().setMyLocationButtonEnabled(false);
            myMap.getUiSettings().setAllGesturesEnabled(true);

//            // Setting Max Zoom level
//            float maxZoom = 15.0f;
//            myMap.setMaxZoomPreference(maxZoom);

            // these don't work. check
            myMap.getUiSettings().setCompassEnabled(true);
            myMap.getUiSettings().setMapToolbarEnabled(true);
            myMap.getUiSettings().setIndoorLevelPickerEnabled(true);


            // Show current location
            getDeviceLocation();

            // Hide waiting progress bar
            toggleItemVisibility(findViewById(R.id.loadingProgressBar), false);


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log("onRequestPermissionsResult", "called");

        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log("onRequestPermissionsResult", "permission failed");

                            return;
                        }
                    }
                    Log("onRequestPermissionsResult", "permission granted");

                    mLocationPermissionsGranted = true;

                    //initialize map
                    initMap();
                }
            }
        }
    }


    // ------------------------------- PERMISSION RELATED METHODS ------------------------------- //
    private void checkPermissions() {
        checkGoogleServicesAvailability();
        checkLocationPermissions();
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

    private void checkLocationPermissions() {

        Log("checkLocationPermissions", "getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        // Request for permissions
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // If permissions granted already
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;

                // Initiate Map since in this case request will not be generated
                initMap();
            } else {

                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    // ------------------------------- INITIALIZATION METHODS ------------------------------- //
    private void uiInitializations() {
        initSearchAutoComplete();
        initRecentSearches();
        initSearchBar();
        initSettingsButton();
        initCurrentLocationButton();
        initDirectionsButton();
        initSourceLocationBar();
        initDestinationBar();
        initBackButton();
        initSwapButton();
    }

    private void initMap() {

        Log("initMap", "initializing map");
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MainActivity.this);
        Log("initMap", "initialized");


    }

    // Override onTouchEvent to hide the keyboard when the map is clicked
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideKeyboard(getCurrentFocus());
        return super.onTouchEvent(event);
    }

    private void initSearchBar() {
        // EditText for place search
        EditText placeSearchEditText = findViewById(R.id.place_search);

        // To show recent searches when the EditText is clicked
        placeSearchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log("initSearchBar", "SearchBar", "onClick");
                searchBarBackButton(true);

                Log("initSearchBar", "SearchBar Text is Empty?", String.valueOf(isEmpty(placeSearchEditText)));

                /////------- ITEMS VISIBILITY  -------/////
                Map<View, Boolean> visibilityMap = new HashMap<>();

                visibilityMap.put(recentSearchLayout, isEmpty(placeSearchEditText)); // Recent Search Layout
                visibilityMap.put(DirectionsButton, false); // Directions button

                allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
            }


        });

        // Add a focus change listener to the EditText
        placeSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log("initSearchBar", "SearchBar", "onFocusChange");
                // Check if the EditText is losing focus
                searchBarBackButton(hasFocus);
                Log("initSearchBar", "SearchBar Has focus?", String.valueOf(hasFocus));

                /////------- ITEMS VISIBILITY  -------/////
                Map<View, Boolean> visibilityMap = new HashMap<>();

                visibilityMap.put(recentSearchLayout, hasFocus); // Recent Search Layout
                if (locationMarked != null) {
                    visibilityMap.put(DirectionsButton, !hasFocus && locationMarked); // Directions button
                }
                allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
            }
        });

        // To show autocomplete suggestions when start typing
        placeSearchEditText.addTextChangedListener(filterTextWatcher);

        // To search a location when the text query submits
        placeSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                Log("initSearchBar", "SearchBar", "onEditorAction");
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // The user pressed "Done" or "Search" on the keyboard
                    String enteredText = textView.getText().toString();
                    Log("initSearchBar", "SearchBar", "Submitted query");

                    /////------- ITEMS VISIBILITY  -------/////
                    Map<View, Boolean> visibilityMap = new HashMap<>();
                    visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
                    visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
                    allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
                    findViewById(R.id.place_search_layout).clearFocus(); // Clear the focus from search bar
                    hideKeyboard(textView); // Hide the keyboard

                    searchLocationByName(enteredText);

                    return true; // Consume the event
                }
                return false; // Continue processing the event
            }
        });

    }

    private void initSearchAutoComplete() {
        recyclerViewForAutocomplete = (RecyclerView) findViewById(R.id.places_recycler_view);

        Places.initialize(this, getResources().getString(R.string.MAPS_API_KEY));

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this);
        recyclerViewForAutocomplete.setLayoutManager(new LinearLayoutManager(this));
        mAutoCompleteAdapter.setClickListener(this);
        recyclerViewForAutocomplete.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();

        Log("initSearchAutoComplete", "initialized");


    }

    private void initRecentSearches() {
        recentSearchLayout = findViewById(R.id.recent_search_layout);
        RecyclerView recyclerViewForRecentSearches = (RecyclerView) findViewById(R.id.recent_search_recycler_view);
        recyclerViewForRecentSearches.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewForRecentSearches.setHasFixedSize(true);

        // close button
        ImageView closeButton = findViewById(R.id.recentSearchCloseButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /////------- ITEMS VISIBILITY  -------/////
                Map<View, Boolean> visibilityMap = new HashMap<>();
                visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
                if (locationMarked != null) {
                    visibilityMap.put(DirectionsButton, locationMarked); // Directions button
                }
                allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map

            }
        });

        RecentSearchAdapter recentSearchAdapter = new RecentSearchAdapter(recyclerViewForRecentSearches);

        recyclerViewForRecentSearches.setAdapter(recentSearchAdapter);
        recentSearchViewModel = new ViewModelProvider(this).get(RecentSearchViewModel.class);
        recentSearchViewModel.getRecentSearches().observe(this, new Observer<List<RecentSearchModel>>() {
            @Override
            public void onChanged(List<RecentSearchModel> recentSearchModels) {
                // update RecyclerView
                Log("initRecentSearches", "onChanged Recent Searches");

                recentSearchAdapter.setRecentSearches(recentSearchModels);
            }
        });

        // Set the item click listener
        recentSearchAdapter.setOnItemClickListener(recentSearch -> {
            // Handle item click, e.g., show the location on the map
            showLocationOfRecentSearch(recentSearch);

//            updateSearchDate(recentSearch);
        });
        Log("initRecentSearches", "initialized");
    }

    private void initSettingsButton() {
        ImageView settingsButton = findViewById(R.id.SettingsButton);

        // Set an OnClickListener for the Settings Button
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the SettingsActivity when the fab is clicked
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });

        Log("initSettingsButton", "initialized");
    }

    private void initCurrentLocationButton() {
        ImageView mGps = findViewById(R.id.ic_gps);
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log("initCurrentLocationButton", "clicked gps icon");
                getDeviceLocation();
            }
        });

        Log("initCurrentLocationButton", "initialized");
    }

    private void initDirectionsButton() {
        try {
            DirectionsButton = findViewById(R.id.directionsButton);
            DirectionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDirections();
                }
            });
        } catch (Exception e) {
            Log("initDirectionsButton", "ERROR", e.getMessage());
            throw new RuntimeException(e);
        }


        Log("initDirectionsButton", "initialized");
    }

    private void initSourceLocationBar() {
        EditText SourceLocationText = findViewById(R.id.sourceLocation);

        // Set up an OnClickListener for the SourceLocationText
        SourceLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /////------- ITEMS VISIBILITY  -------/////
                Map<View, Boolean> visibilityMap = new HashMap<>();

                visibilityMap.put(findViewById(R.id.SearchBarRelLayout), false);  // Search bar
                visibilityMap.put(recentSearchLayout, true); // Recent Search Layout
                visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
                visibilityMap.put(DirectionsButton, false); // Directions button

                allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
            }
        });
        Log("initSourceLocationBar", "initialized");
    }

    private void initDestinationBar() {
    }

    private void initBackButton() {
        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Clear Polylines
                clearDirectionRoute();
                locationMarked = true;

                /////------- ITEMS VISIBILITY  -------/////
                Map<View, Boolean> visibilityMap = new HashMap<>();

                visibilityMap.put(findViewById(R.id.SearchBarRelLayout), true);  // Search bar
                visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
                visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
                if (locationMarked != null) {
                    visibilityMap.put(DirectionsButton, locationMarked); // Directions button
                }
                visibilityMap.put(findViewById(R.id.directionBar), false); // Direction Bar

                allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
                findViewById(R.id.place_search_layout).clearFocus(); // Clear the focus from search bar

            }
        });

        Log("initBackButton", "initialized");
    }

    private void initSwapButton() {
        ImageButton swapButton = findViewById(R.id.SwapButton);
        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                swapLocations();
            }
        });

        Log("initSwapButton", "initialized");
    }


    // ------------------------------- HELPER METHODS ------------------------------- //

    // ------------- Search by name ------------- //
    private void searchLocationByName(String enteredText) {

        if (enteredText == null || enteredText.isEmpty()) {
            Log("searchLocationByName", "Empty query submitted");
            Toast.makeText(MainActivity.this, "Please enter a place name", Toast.LENGTH_SHORT).show();
            return;
        }

        Log("searchLocationByName", "Query submitted", enteredText);


        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Address> addressList = performGeocode(enteredText);

            runOnUiThread(() -> handleGeocodeResults(addressList));
        });
    }

    private List<Address> performGeocode(String enteredText) {
        if (enteredText == null || enteredText.isEmpty()) {
            return null;
        }

        Geocoder geocoder = new Geocoder(MainActivity.this);

        try {
            return geocoder.getFromLocationName(enteredText, 1);
        } catch (IOException e) {
            Log("performGeocode: Error: ", e.getMessage());
            return null;
        }
    }

    private void handleGeocodeResults(List<Address> addressList) {
        if (addressList == null) {
            Toast.makeText(MainActivity.this, "An error occurred while searching for the place.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!addressList.isEmpty()) {
            Address address = addressList.get(0);
            Log("handleGeocodeResults", "Found a location", address.toString());

            if (address.getAddressLine(0) != null && !(address.getAddressLine(0).isEmpty()) && new LatLng(address.getLatitude(), address.getLongitude()) != null) {

                DestinationLocationName = address.getAddressLine(0);
                DestinationLocationLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                TextInputEditText placeSearchEditText = findViewById(R.id.place_search);
                placeSearchEditText.setText(DestinationLocationName);

                saveToRecentSearches(DestinationLocationName, DestinationLocationLatLng);
                moveCamera(DestinationLocationLatLng, DEFAULT_ZOOM, DestinationLocationName);

                // Show the Directions button
                locationMarked = true;

                /////------- ITEMS VISIBILITY  -------/////
                Map<View, Boolean> visibilityMap = new HashMap<>();

                visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
                visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
                visibilityMap.put(DirectionsButton, true); // Directions button

                allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
                findViewById(R.id.place_search_layout).clearFocus(); // Clear the focus from search bar
            }


        } else {
            Log("handleGeocodeResults", "No results found");
            Toast.makeText(MainActivity.this, "No places found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchBarBackButton(Boolean showBackBtn) {
        TextInputLayout placeSearchLayout = findViewById(R.id.place_search_layout);
        TextInputEditText placeSearchEditText = findViewById(R.id.place_search);


        if (showBackBtn) {
            // set the back button icon
            placeSearchLayout.setStartIconDrawable(R.drawable.button_back_24);

            // Add the click listener to the back button
            placeSearchLayout.setStartIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Clear text
                    placeSearchEditText.setText("");

                    // Lose focus from the search bar
                    placeSearchLayout.clearFocus();

                    // Hide the keyboard if it's currently visible
                    hideKeyboard(placeSearchEditText);
                }
            });
        } else {
            // set the search icon
            placeSearchLayout.setStartIconDrawable(com.google.android.gms.location.places.R.drawable.places_ic_search);
        }

    }

    // ------------- Search by autocomplete suggestions ------------- //
    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean isTextEmpty = s.toString().equals("");
            if (!isTextEmpty) {
                //if a letter is typed
                mAutoCompleteAdapter.getFilter().filter(s.toString());
            }

            /////------- ITEMS VISIBILITY  -------/////
            Map<View, Boolean> visibilityMap = new HashMap<>();

            visibilityMap.put(recentSearchLayout, isTextEmpty); // Recent Search Layout
            visibilityMap.put(recyclerViewForAutocomplete, !isTextEmpty); // Autocomplete Layout
            visibilityMap.put(DirectionsButton, false); // Directions button

            allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    public void handleAutocompleteItemClick(Place place) {
        if (place.getName() != null && !(place.getName().isEmpty()) && place.getLatLng() != null) {
            TextInputEditText placeSearchEditText = findViewById(R.id.place_search);
            placeSearchEditText.setText(place.getName());

            DestinationLocationName = place.getName();
            DestinationLocationLatLng = place.getLatLng();
            Log("handleAutocompleteItemClick", "Place", place.getAddress() + ", " + DestinationLocationLatLng.latitude + DestinationLocationLatLng.longitude);

            saveToRecentSearches(DestinationLocationName, DestinationLocationLatLng);

            moveCamera(place.getLatLng(), DEFAULT_ZOOM, place.getName());

            locationMarked = true;

            /////------- ITEMS VISIBILITY  -------/////
            Map<View, Boolean> visibilityMap = new HashMap<>();

            visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
            visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
            visibilityMap.put(DirectionsButton, true); // Directions button

            allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
            findViewById(R.id.place_search_layout).clearFocus(); // Clear the focus from search bar
            hideKeyboard(findViewById(R.id.place_search)); // Hide the keyboard
        }
    }

    // ------------- Recent searches ------------- //
    private void saveToRecentSearches(String addressName, LatLng latLng) {
        Date currentDate = new Date();
        RecentSearchModel newRecentSearch = new RecentSearchModel(addressName, latLng, currentDate);
        recentSearchViewModel.insert(newRecentSearch);
        Log("saveToRecentSearches", "Last search saved");
    }

    private void updateSearchDate(RecentSearchModel recentSearch) {
        Date currentDate = new Date();
        int id = recentSearch.getSearchId();
        String locationName = recentSearch.getLocationName();
        LatLng locationLatLng = recentSearch.getLocationLatLang();

        RecentSearchModel updatedRecentSearch = new RecentSearchModel(locationName, locationLatLng, currentDate);
        // ID should be set otherwise room can't identify the item and will not be updated.
        updatedRecentSearch.setSearchId(id);
        recentSearchViewModel.update(updatedRecentSearch);
        Log("updateSearchDate", "Date updated");
    }

    private void showLocationOfRecentSearch(RecentSearchModel recentSearch) {
        saveToRecentSearches(recentSearch.getLocationName(), recentSearch.getLocationLatLang());

        if (recentSearch.getLocationLatLang() != null && recentSearch.getLocationName() != null && !(recentSearch.getLocationName().isEmpty())) {

            DestinationLocationLatLng = recentSearch.getLocationLatLang();
            DestinationLocationName = recentSearch.getLocationName();

            TextInputLayout placeSearchLayout = findViewById(R.id.place_search_layout);
            TextInputEditText placeSearchEditText = findViewById(R.id.place_search);
            placeSearchEditText.setText(DestinationLocationName);

            moveCamera(DestinationLocationLatLng, DEFAULT_ZOOM, DestinationLocationName);

            Log("showLocationOfRecentSearch", "Recently searched place located on the map");

            // Show the Directions button
            locationMarked = true;

        } else {
            Toast.makeText(MainActivity.this, "Unable to get recent location", Toast.LENGTH_SHORT).show();
            Log("showLocationOfRecentSearch", "ERROR", "unable to get recent location");
        }

        /////------- ITEMS VISIBILITY  -------/////
        Map<View, Boolean> visibilityMap = new HashMap<>();


        visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
        visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
        visibilityMap.put(DirectionsButton, true); // Directions button

        allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
        findViewById(R.id.place_search_layout).clearFocus(); // Clear the focus from search bar
        hideKeyboard(recentSearchLayout); // Hide the keyboard

    }


    // ------------- Show a location on map ------------- //
    private void getDeviceLocation() {

        Log("getDeviceLocation", "getting the devices current location");


        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log("getDeviceLocation", "found location!");

                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                Log("getDeviceLocation", "found location", currentLocation.toString());
                                SourceLocationName = "Your current location";
                                SourceLocationLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                // Did not use moveCamera method here since it will mark the current location by a marker
                                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM));

                            } else {
                                Log("getDeviceLocation", "ERROR", "current location is null");

                                Toast.makeText(MainActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log("getDeviceLocation", "ERROR", "current location is null");
                            Toast.makeText(MainActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log("getDeviceLocation", "ERROR", "SecurityException: " + e.getMessage());

        }
    }

    private void moveCamera(LatLng location, float zoom, String title) {
        if (myMap != null) {

            Log("moveCamera", "moving the camera", "lat: " + location.latitude + ", lng: " + location.longitude);

            // Remove any existing markers
            myMap.clear();

            // Move camera to the location and set the desired zoom level
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));

            // Marker options
            MarkerOptions options = new MarkerOptions().position(location).title(title);

            // Add marker to the location
            myMap.addMarker(options);
        }
    }

    // ------------- Draw directions on map ------------- //

    private void showDirections() {

        // TODO: 2023-12-07  SOURCE and DESTINATION LATLANG,NAMES should be passed in correct MVVM path

        Log("showDirections", "started showing directions");

        if (SourceLocationLatLng != null
                && DestinationLocationLatLng != null
                && SourceLocationName != null
                && DestinationLocationName != null) {

            direction(SourceLocationName, SourceLocationLatLng, DestinationLocationName, DestinationLocationLatLng);

            /////------- ITEMS VISIBILITY  -------/////
            Map<View, Boolean> visibilityMap = new HashMap<>();

            visibilityMap.put(findViewById(R.id.SearchBarRelLayout), false);  // Search bar
            visibilityMap.put(DirectionsButton, false); // Directions button
            visibilityMap.put(findViewById(R.id.directionBar), true); // Direction Bar

            allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map


        }

    }

    private void direction(String originName, LatLng origin, String destinationName, LatLng destination) {

        // set Location bars
        EditText SourceLocation = findViewById(R.id.sourceLocation);
        EditText DestinationLocation = findViewById(R.id.destinationLocation);

        SourceLocation.setText(originName);
        DestinationLocation.setText(destinationName);

        // Construct the URL for the Google Directions API request
        String apiKey = "AIzaSyDDTamV9IieqbDXoWKxjEHmBo7jRcNuhFg"; // Replace with your actual API key
        String url = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&key=" + apiKey;

        // Create a request to the Directions API
        JsonObjectRequest directionsRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse the JSON response
                            JSONArray routes = response.getJSONArray("routes");
                            if (routes.length() > 0) {
                                JSONObject route = routes.getJSONObject(0);
                                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                                String encodedPolyline = overviewPolyline.getString("points");

                                // Decode the polyline and add it to the map as a Polyline
                                List<LatLng> decodedPolyline = decodePolyline(encodedPolyline);
                                PolylineOptions polylineOptions = new PolylineOptions()
                                        .addAll(decodedPolyline)
                                        .width(15)
                                        .color(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));

                                directionRoutePolyline = myMap.addPolyline(polylineOptions);

                                locationMarked = false;

                                // After adding the polyline, adjust the camera to fit the entire route
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng point : decodedPolyline) {
                                    builder.include(point);
                                }

                                LatLngBounds bounds = builder.build();
                                int padding = 100; // Adjust this padding as needed

                                // Animate the camera to fit the entire route with padding
                                myMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

                            } else {
                                Toast.makeText(MainActivity.this, "No routes found.", Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing directions.", Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error fetching directions.", Toast.LENGTH_SHORT).show();

                    }
                });

        // Set a retry policy for the request
        RetryPolicy retryPolicy = new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        directionsRequest.setRetryPolicy(retryPolicy);

        // Add the request to the Volley request queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(directionsRequest);


    }

    // TODO: 2023-12-07  MAKE FONT STYLE instead of using bold etc


    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> points = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng point = new LatLng(lat / 1E5, lng / 1E5);
            points.add(point);
        }

        return points;
    }

    // Method to clear or hide the direction route
    private void clearDirectionRoute() {
        if (directionRoutePolyline != null) {
            directionRoutePolyline.remove();
            //directionRoutePolyline = null;
        }
    }

    private void swapLocations() {

        // TODO: 2023-12-07 CLEAR THIS MESS 

        clearDirectionRoute();

        EditText SourceLocation = findViewById(R.id.sourceLocation);
        EditText DestinationLocation = findViewById(R.id.destinationLocation);
        Editable temp;
        LatLng tempLatLng;

        Log("swapLocations", "Before SourceLocationLatLng", SourceLocationLatLng.toString());
        Log("swapLocations", "Before DestinationLocationLatLng", DestinationLocationLatLng.toString());

        if (SourceLocation.getText() != null && DestinationLocation.getText() != null) {
            temp = SourceLocation.getText();
            SourceLocation.setText(DestinationLocation.getText());
            DestinationLocation.setText(temp);

            tempLatLng = SourceLocationLatLng;
            SourceLocationLatLng = DestinationLocationLatLng;
            DestinationLocationLatLng = tempLatLng;

            Log("swapLocations", "SourceLocation.getText().toString()", SourceLocation.getText().toString());
            Log("swapLocations", "DestinationLocation.getText().toString()", DestinationLocation.getText().toString());

            direction(SourceLocation.getText().toString(), SourceLocationLatLng, DestinationLocation.getText().toString(), DestinationLocationLatLng);


        }
    }

    // ORIGINAL METHODS - CODE

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
                Log("toggleItemsVisibility", view.toString(), String.valueOf(isVisible));
            }
        }
    }

    private void hideKeyboard(View view) {

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    // ------------------------------- DRAFTS ------------------------------- //
    private void virtualTicket() {

        Intent intent = new Intent(this, virtualTicket.class);
        startActivity(intent);
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    private Circle addCircleToMap(LatLng centerLatLng, double radius) {
        CircleOptions circleOptions = new CircleOptions()
                .center(centerLatLng)
                .radius(radius) // Radius in meters
                .strokeWidth(2) // Width of the circle's outline
                .strokeColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)) // Color of the outline
                .fillColor(ContextCompat.getColor(MainActivity.this, R.color.circleFill)); // Fill color of the circle

        // Add the circle to the map
        Circle circle = myMap.addCircle(circleOptions);

        // Set a click listener for the circle
        circle.setClickable(true);
        circle.setTag("Your circle's tag"); // Optional: You can set a tag to identify the circle

        return circle;
    }


}


