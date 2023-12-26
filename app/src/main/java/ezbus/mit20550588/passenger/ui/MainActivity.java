package ezbus.mit20550588.passenger.ui;

import static ezbus.mit20550588.passenger.util.Constants.Log;
import static ezbus.mit20550588.passenger.util.Converters.timestampToFormattedTime;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.StyleSpan;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.data.model.BusLocationModel;
import ezbus.mit20550588.passenger.data.model.RecentSearchModel;
import ezbus.mit20550588.passenger.data.model.TicketModel;
import ezbus.mit20550588.passenger.data.viewModel.BusLocationViewModel;

import ezbus.mit20550588.passenger.data.viewModel.RecentSearchViewModel;
import ezbus.mit20550588.passenger.data.viewModel.TicketViewModel;
import ezbus.mit20550588.passenger.ui.Login.Login;
import ezbus.mit20550588.passenger.ui.PurchaseTicket.PurchaseTicket;
import ezbus.mit20550588.passenger.ui.Settings.Settings;
import ezbus.mit20550588.passenger.ui.adapters.PlacesAutoCompleteAdapter;
import ezbus.mit20550588.passenger.ui.adapters.RecentSearchAdapter;
import ezbus.mit20550588.passenger.util.DateUtils;
import ezbus.mit20550588.passenger.util.FullWidthDialog;
import ezbus.mit20550588.passenger.util.UserStateManager;


public class MainActivity
        extends AppCompatActivity
        implements OnMapReadyCallback, PlacesAutoCompleteAdapter.ClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.InfoWindowAdapter {

    // ------------------------------- DECLARING VARIABLES ------------------------------- //

    // -------------- widgets -------------- //
    private GoogleMap myMap;

    // -------------- constants -------------- //
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    // -------------- variables -------------- //

    // ---- permissions ---- //
    private SharedPreferences preferences;
    private Boolean mLocationPermissionsGranted = false;

    // ---- searches ---- //
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private RecyclerView recyclerViewForAutocomplete;
    private RelativeLayout recentSearchLayout;
    private RecentSearchViewModel recentSearchViewModel;
    private Integer after_LocatingByRecentSearch_Method;
    private Integer after_LocatingByAutocomplete_Method;

    // ---- directions ---- //
    private LatLng CurrentLocationLatLng;
    private LatLng SourceLocationLatLng;
    private LatLng DestinationLocationLatLng;
    private String SourceLocationName = "Origin placeholder";
    private String DestinationLocationName = "Destination placeholder";
    private Polyline directionRoutePolyline;
    private Button DirectionsButton;
    private Boolean locationMarked;

    // ---- bus locations ---- //
    private BusLocationViewModel busLocationViewModel;
    private Set<String> availableBusIds = new HashSet<>();
    private ArrayList<BusLocationModel> availableBusList = new ArrayList<>();
    private Map<String, Marker> busMarkers = new HashMap<>();

    // ---- bus fare ---- //
    private TicketViewModel ticketViewModel;
    private ArrayList<TicketModel> ticketList = new ArrayList<>();

    // ---- map full screen ---- //
    private boolean isMapExpanded = false;
    private int initialDirectionBarHeight;
    private int initialMapAndSearchBarHeight;
    private int targetHeight;

    // ------------------------------- LIFECYCLE METHODS ------------------------------- //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        authentication();

        checkPermissions();

        uiInitializations();

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

        clearBusMapMarkers();

        Log("onStop", "MainActivity stopped");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release resources, unregister listeners, etc. before the activity is destroyed
        // Example: If your app is using any resources or services that need to be released explicitly, such as stopping a location tracking service, you might do so in the onDestroy method.
        // Remove any existing markers

        clearBusMapMarkers();

        Log("onDestroy", "MainActivity destroyed");
    }


    // ------------------------------- CALLBACK METHODS ------------------------------- //
    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log("onMapReady", "map is ready");


        myMap = googleMap;

        // Click listener for map to hide other elements when it's clicked
        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                // Hide other items when the map is clicked

                Log("onMapReady", "onMapClick", "Clicked");

                /////------- ITEMS VISIBILITY  -------/////
                Map<View, Boolean> visibilityMap = new HashMap<>();

                visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
                visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
                visibilityMap.put(findViewById(R.id.busDetails_RelativeLayout), false); // Bus details banner
                visibilityMap.put(findViewById(R.id.current_location_layout), false);

                allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
                TextInputLayout placeSearchLayout = findViewById(R.id.place_search_layout);
                placeSearchLayout.clearFocus();  // Clear the focus from search bar
                hideKeyboard(getCurrentFocus());
            }
        });

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
            myMap.getUiSettings().setMyLocationButtonEnabled(false);

//            // Setting Max Zoom level
//            float maxZoom = 15.0f;
//            myMap.setMaxZoomPreference(maxZoom);

            // these don't work. check
            myMap.getUiSettings().setCompassEnabled(true);
            myMap.getUiSettings().setMapToolbarEnabled(true);
            myMap.getUiSettings().setIndoorLevelPickerEnabled(true);

            // Zoom into the Sri Lanka
            setMapBoundary();

            // Show current location
            getDeviceLocation();

            // Hide waiting progress bar
            toggleItemVisibility(findViewById(R.id.loadingProgressBar), false);

            // Set the marker click listener
            myMap.setOnMarkerClickListener(this);

            // Set the info window adapter
            myMap.setInfoWindowAdapter(this);

            // Set info window click listener to handle the entire info window click
            myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    // Handle the entire info window click here
                    String startingStop = "Bus Stop A";
                    String endingStop = "Bus Stop B";

                    Intent intent = new Intent(MainActivity.this, virtualTicket.class);
                    intent.putExtra("startingStop", startingStop);
                    intent.putExtra("endingStop", endingStop);
                    startActivity(intent);
                }
            });

            // Set marker click listener
            myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker != null) {
                        String markerTitle = marker.getTitle();
                        LatLng markerPosition = marker.getPosition();
                        String markerId = marker.getId();
                        // Retrieve the tag set for the marker
                        Object tag = marker.getTag();

                        if (tag instanceof String) {
                            // The tag is a String, so you can cast it and use it
                            String busId = (String) tag;
                            // Find the corresponding Bus in the availableBusList
                            BusLocationModel correspondingBus = findBusById(busId);
                            if (correspondingBus != null) {
                                Log("Marker Clicked", "Corresponding Bus: " + correspondingBus);
                                showBusDetails(correspondingBus);
                            }
                        }
                    }
                    return true; // Return true to consume the click event
                }
            });
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
    private void authentication(){

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
                            return true;
                        } else {
                            // User is not authenticated, start the authentication flow
                            Intent authIntent = new Intent(MainActivity.this, Login.class);
                            startActivity(authIntent);
                            // Finish the MainActivity to prevent it from being shown to the user
                            finish();
                            // Return false to suspend drawing until the authentication flow completes
                            return false;
                        }

//                        } else {
//                            // The content isn't ready. Suspend.
//                            return false;
//                        }
                    }
                });
    }

    private boolean isUserAuthenticated() {
        // Retrieve the authentication token from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        String authToken = preferences.getString("authToken", "");

        // Check if the token exists and is not empty
        return !TextUtils.isEmpty(authToken);
    }

    private void uiInitializations() {
        initSearchAutoComplete();
        initRecentSearches();
        initSearchBar();
        initSettingsButton();
        initCurrentLocationButton();
        initFullScreenMapButton();
        initDirectionsButton();
        initBusLocationView();
        initTicketView();
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
        /////------- ITEMS VISIBILITY  -------/////
        Map<View, Boolean> visibilityMap = new HashMap<>();

        visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
        visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
        visibilityMap.put(findViewById(R.id.current_location_layout), false); // hide current location banner

        allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map

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

                after_LocatingByRecentSearch_Method = 1;


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

                after_LocatingByRecentSearch_Method = 1;

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
        placeSearchEditText.addTextChangedListener(filterTextWatcherForSearchBar);

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

                    searchLocationByName(enteredText, 1);

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
                visibilityMap.put((View) findViewById(R.id.current_location_layout), false); // hide current location banner
                if (locationMarked != null) {
                    visibilityMap.put(DirectionsButton, locationMarked); // Directions button
                }
                allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map

            }
        });

        RecentSearchAdapter recentSearchAdapter = new RecentSearchAdapter();

        recyclerViewForRecentSearches.setAdapter(recentSearchAdapter);
        recentSearchAdapter.setRecyclerView(recyclerViewForRecentSearches);
        recentSearchViewModel = new ViewModelProvider(this).get(RecentSearchViewModel.class);
        recentSearchViewModel.getRecentSearches().observe(this, new Observer<List<RecentSearchModel>>() {
            @Override
            public void onChanged(List<RecentSearchModel> recentSearchModels) {
                // update RecyclerView
                Log("initRecentSearches", "onChanged Recent Searches");

                recentSearchAdapter.submitList(recentSearchModels);
                recentSearchAdapter.scrollToTop();
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
               // Intent intent = new Intent(MainActivity.this, CheckoutActivity.class);
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
                if (CurrentLocationLatLng != null ){
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CurrentLocationLatLng,
                            DEFAULT_ZOOM));
                }

            }
        });

        Log("initCurrentLocationButton", "initialized");
    }

    private void initFullScreenMapButton() {
        Log("initFullScreenMapButton", "started");

        RelativeLayout directionBar = findViewById(R.id.directionBar);
        RelativeLayout mapAndSearchContainer = findViewById(R.id.mapAndSearchContainer);


        mapAndSearchContainer.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        targetHeight = mapAndSearchContainer.getMeasuredHeight();

        // Get the initial height of 'directionBar'
        initialDirectionBarHeight = directionBar.getHeight();
        initialMapAndSearchBarHeight = directionBar.getHeight();

        ImageView fullScreenButton = findViewById(R.id.full_screen_button);
        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //clearBusMapMarkers();

                Log("initFullScreenMapButton", "clicked");
                if (isMapExpanded) {
                    // collapseView();
                    toggleItemVisibility(directionBar, true);
                } else {
                    //expandView();
                    toggleItemVisibility(directionBar, false);
                }
                isMapExpanded = !isMapExpanded;
                Log("initFullScreenMapButton", "ended");
            }
        });

        Log("initFullScreenMapButton", "ended");
    }
//
//    private int getTargetHeight() {
//        // Measure the view with unspecified height to get the "wrap_content" height
//        RelativeLayout mapAndSearchContainer = findViewById(R.id.mapAndSearchContainer);
//        mapAndSearchContainer.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        return mapAndSearchContainer.getMeasuredHeight();
//    }
//
//    private void expandView() {
//        Log("expandView", "initialized");
//        RelativeLayout directionBar = findViewById(R.id.directionBar);
//        RelativeLayout mapAndSearchContainer = findViewById(R.id.mapAndSearchContainer);
//
//
//
//        if (directionBar == null || mapAndSearchContainer == null) {
//            // Handle the case where views are not found
//            return;
//        }
//
//        // Expand the map to full screen
//        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mapAndSearchContainer);
//        startHeightAnimation(mapAnimationWrapper, initialMapAndSearchBarHeight, targetHeight);
//
//        // Hide the 'directionBar' by setting its height to 0
//        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(directionBar);
//        startHeightAnimation(recyclerAnimationWrapper, initialDirectionBarHeight, 0);
//
//        Log("expandView", "ended");
//    }
//
//    private void collapseView() {
//        Log("collapseView", "initialized");
//        RelativeLayout directionBar = findViewById(R.id.directionBar);
//        RelativeLayout mapAndSearchContainer = findViewById(R.id.mapAndSearchContainer);
//
//        if (directionBar == null || mapAndSearchContainer == null) {
//            // Handle the case where views are not found
//            return;
//        }
//
//        // Contract the map to half of the screen
//        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mapAndSearchContainer);
//        startHeightAnimation(mapAnimationWrapper, targetHeight, initialMapAndSearchBarHeight);
//
//        // Show the 'directionBar' by setting its height to its initial height
//        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(directionBar);
//        startHeightAnimation(recyclerAnimationWrapper, 0, initialDirectionBarHeight);
//        Log("collapseView", "ended");
//    }
//
//    private void startHeightAnimation(ViewWeightAnimationWrapper wrapper, int startHeight, int endHeight) {
//        Log("startHeightAnimation", "initialized");
//        ValueAnimator animator = ValueAnimator.ofInt(startHeight, endHeight);
//        animator.setDuration(800);
//        animator.addUpdateListener(animation -> {
//            int value = (int) animation.getAnimatedValue();
//            wrapper.setHeight(value);
//        });
//        animator.start();
//        Log("startHeightAnimation", "ended");
//    }


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

    private void initBusLocationView() {
        // Initialize ViewModel and Repository
        busLocationViewModel = new ViewModelProvider(this).get(BusLocationViewModel.class);
    }

    private void initTicketView() {
        // Initialize ViewModel and Repository
        ticketViewModel = new ViewModelProvider(this).get(TicketViewModel.class);
    }

    private void initSourceLocationBar() {

        Log("initSourceLocationBar", "initialized");

        // EditText for source location
        EditText SourceLocationText = findViewById(R.id.sourceLocationText);

        // To show recent searches when the EditText is clicked
        SourceLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log("initSourceLocationBar", "onClick");

                after_LocatingByRecentSearch_Method = 2;
                initCurrentLocationOption("Source");

                Log("initSourceLocationBar", "SearchBar Text is Empty?", String.valueOf(isEmpty(SourceLocationText)));

                /////------- ITEMS VISIBILITY  -------/////
                Map<View, Boolean> visibilityMap = new HashMap<>();

                visibilityMap.put(findViewById(R.id.SearchBarRelLayout), false);  // Search bar
                visibilityMap.put(recentSearchLayout, true); // Recent Search Layout
                visibilityMap.put(DirectionsButton, false); // Directions button
                visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
                visibilityMap.put(findViewById(R.id.current_location_layout), true); // Current location

                allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
            }
        });

        // Add a focus change listener to the EditText
        SourceLocationText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Check if the EditText is losing focus
                Log("initSourceLocationBar", "SearchBar Has focus?", String.valueOf(hasFocus));

                after_LocatingByRecentSearch_Method = 2;
                initCurrentLocationOption("Source");

                /////------- ITEMS VISIBILITY  -------/////
                Map<View, Boolean> visibilityMap = new HashMap<>();
                visibilityMap.put(recentSearchLayout, hasFocus); // Recent Search Layout
                visibilityMap.put(findViewById(R.id.current_location_layout), hasFocus); // Current location
                allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
            }
        });

        // To show autocomplete suggestions when start typing
        SourceLocationText.addTextChangedListener(filterTextWatcherForSourceLocationBar);

        // To search a location when the text query submits
        SourceLocationText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                Log("initSourceLocationBar", "SearchBar", "onEditorAction");
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // The user pressed "Done" or "Search" on the keyboard
                    String enteredText = textView.getText().toString();
                    Log("initSourceLocationBar", "SearchBar", "Submitted query");

                    /////------- ITEMS VISIBILITY  -------/////
                    Map<View, Boolean> visibilityMap = new HashMap<>();
                    visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
                    visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
                    visibilityMap.put(findViewById(R.id.current_location_layout), false); // hide current location banner
                    allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
                    findViewById(R.id.sourceLocationText).clearFocus(); // Clear the focus from search bar
                    hideKeyboard(textView); // Hide the keyboard

                    searchLocationByName(enteredText, 2);

                    return true; // Consume the event
                }
                return false; // Continue processing the event
            }
        });

    }

    private void initDestinationBar() {

        Log("initDestinationBar", "initialized");

        // EditText for source location
        EditText DestinationLocationText = findViewById(R.id.destinationLocationText);

        // To show recent searches when the EditText is clicked
        DestinationLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log("initDestinationBar", "onClick");

                after_LocatingByRecentSearch_Method = 3;
                initCurrentLocationOption("Destination");

                Log("initDestinationBar", "SearchBar Text is Empty?", String.valueOf(isEmpty(DestinationLocationText)));

                /////------- ITEMS VISIBILITY  -------/////
                Map<View, Boolean> visibilityMap = new HashMap<>();

                visibilityMap.put(findViewById(R.id.SearchBarRelLayout), false);  // Search bar
                visibilityMap.put(recentSearchLayout, true); // Recent Search Layout
                visibilityMap.put(DirectionsButton, false); // Directions button
                visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
                visibilityMap.put(findViewById(R.id.current_location_layout), true); // Current location

                allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
            }
        });

        // Add a focus change listener to the EditText
        DestinationLocationText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Check if the EditText is losing focus
                Log("initDestinationBar", "SearchBar Has focus?", String.valueOf(hasFocus));

                after_LocatingByRecentSearch_Method = 3;
                initCurrentLocationOption("Destination");

                /////------- ITEMS VISIBILITY  -------/////
                Map<View, Boolean> visibilityMap = new HashMap<>();
                visibilityMap.put(recentSearchLayout, hasFocus); // Recent Search Layout
                visibilityMap.put(findViewById(R.id.current_location_layout), hasFocus); // Current location
                allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
            }
        });

        // To show autocomplete suggestions when start typing
        DestinationLocationText.addTextChangedListener(filterTextWatcherForDestinationBar);

        // To search a location when the text query submits
        DestinationLocationText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                Log("initDestinationBar", "SearchBar", "onEditorAction");
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // The user pressed "Done" or "Search" on the keyboard
                    String enteredText = textView.getText().toString();
                    Log("initDestinationBar", "SearchBar", "Submitted query");

                    /////------- ITEMS VISIBILITY  -------/////
                    Map<View, Boolean> visibilityMap = new HashMap<>();
                    visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
                    visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
                    visibilityMap.put(findViewById(R.id.current_location_layout), false); // hide current location banner
                    allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
                    DestinationLocationText.clearFocus(); // Clear the focus from search bar
                    hideKeyboard(textView); // Hide the keyboard

                    searchLocationByName(enteredText, 3);

                    return true; // Consume the event
                }
                return false; // Continue processing the event
            }
        });
    }


    private void initCurrentLocationOption(String SourceOrDestination) {
        Log("initCurrentLocationOption", "Started");
        TextView currentLocationText = findViewById(R.id.current_location_text_button);
        currentLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SourceOrDestination == null) {
                    Log("initCurrentLocationOption", "NULL", "Clicked");
                    SourceLocationName = "Your current location";
                    SourceLocationLatLng = CurrentLocationLatLng;
                    EditText SourceLocation = findViewById(R.id.sourceLocationText);
                    SourceLocation.setText("Your current location");
                } else if (SourceOrDestination == "Source") {
                    Log("initCurrentLocationOption", "Source", "Clicked");
                    SourceLocationName = "Your current location";
                    SourceLocationLatLng = CurrentLocationLatLng;
                    EditText SourceLocation = findViewById(R.id.sourceLocationText);
                    SourceLocation.setText("Your current location");
                } else if (SourceOrDestination == "Destination") {
                    Log("initCurrentLocationOption", "Destination", "Clicked");
                    DestinationLocationName = "Your current location";
                    DestinationLocationLatLng = CurrentLocationLatLng;
                    EditText DestinationLocation = findViewById(R.id.destinationLocationText);
                    DestinationLocation.setText("Your current location");
                }

                // Redraw the Directions
                Log("initCurrentLocationOption", "Source,Destination", SourceLocationName + DestinationLocationName);
                direction(SourceLocationLatLng, DestinationLocationLatLng);

                /////------- ITEMS VISIBILITY  -------/////
                Map<View, Boolean> visibilityMap = new HashMap<>();

                visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
                visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
                visibilityMap.put(DirectionsButton, false); // Directions button
                visibilityMap.put((findViewById(R.id.current_location_layout)), false); // current location

                allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
                findViewById(R.id.sourceLocationInputLayout).clearFocus(); // Clear the focus from search bar
                findViewById(R.id.destinationLocationInputLayout).clearFocus(); // Clear the focus from search bar
                hideKeyboard(currentLocationText);

            }
        });
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
    private void searchLocationByName(String enteredText, int after_LocatingByName_Method) {

        // int after_LocatingByName_Method == what should do after showing locating the place
        // 1 for --- search bar
        // 2 for --- source location bar
        // 3 for --- destination bar

        if (enteredText == null || enteredText.isEmpty()) {
            Log("searchLocationByName", "Empty query submitted");
            Toast.makeText(MainActivity.this, "Please enter a place name", Toast.LENGTH_SHORT).show();
            return;
        }

        Log("searchLocationByName", "Query submitted", enteredText);


        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Address> addressList = performGeocode(enteredText);

            runOnUiThread(() -> handleGeocodeResults(addressList, after_LocatingByName_Method));
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

    private void handleGeocodeResults(List<Address> addressList, int after_LocatingByName_Method) {
        if (addressList == null) {
            Toast.makeText(MainActivity.this, "An error occurred while searching for the place.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!addressList.isEmpty()) {
            Address address = addressList.get(0);
            Log("handleGeocodeResults", "Found a location", address.toString());

            if (address.getAddressLine(0) != null && !(address.getAddressLine(0).isEmpty()) && new LatLng(address.getLatitude(), address.getLongitude()) != null) {

                String locationName = address.getAddressLine(0);
                LatLng locationLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                saveToRecentSearches(locationName, locationLatLng);
                //moveCamera(locationLatLng, DEFAULT_ZOOM, locationName);

                // what should do after locating the place
                // 1 for --- search bar
                // 2 for --- source location bar
                // 3 for --- destination bar
                if (after_LocatingByName_Method == 1) {
                    DestinationLocationName = locationName;
                    DestinationLocationLatLng = locationLatLng;
                    afterLocatingByNameOnSearchBar(locationName);
                } else if (after_LocatingByName_Method == 2) {
                    SourceLocationName = locationName;
                    SourceLocationLatLng = locationLatLng;
                    afterLocatingByNameOnSourceBar(locationName);
                } else if (after_LocatingByName_Method == 3) {
                    DestinationLocationName = locationName;
                    DestinationLocationLatLng = locationLatLng;
                    afterLocatingByNameOnDestinationBar(locationName);
                }

                // Redraw directions
                direction(SourceLocationLatLng, DestinationLocationLatLng);
            }
        } else {
            Log("handleGeocodeResults", "No results found");
            Toast.makeText(MainActivity.this, "No places found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void afterLocatingByNameOnSearchBar(String locationName) {

        TextInputEditText placeSearchEditText = findViewById(R.id.place_search);
        placeSearchEditText.setText(locationName);

        locationMarked = true;

        /////------- ITEMS VISIBILITY  -------/////
        Map<View, Boolean> visibilityMap = new HashMap<>();

        visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
        visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
        visibilityMap.put(DirectionsButton, true); // Directions button

        allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
        findViewById(R.id.place_search_layout).clearFocus(); // Clear the focus from search bar
    }

    private void afterLocatingByNameOnSourceBar(String locationName) {
        EditText sourceSearchEditText = findViewById(R.id.sourceLocationText);
        sourceSearchEditText.setText(locationName);

        /////------- ITEMS VISIBILITY  -------/////
        Map<View, Boolean> visibilityMap = new HashMap<>();

        visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
        visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
        visibilityMap.put(DirectionsButton, false); // Directions button

        allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
        sourceSearchEditText.clearFocus(); // Clear the focus from search bar
    }

    private void afterLocatingByNameOnDestinationBar(String locationName) {
        EditText destinationSearchEditText = findViewById(R.id.destinationLocationText);
        destinationSearchEditText.setText(locationName);

        /////------- ITEMS VISIBILITY  -------/////
        Map<View, Boolean> visibilityMap = new HashMap<>();

        visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
        visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
        visibilityMap.put(DirectionsButton, false); // Directions button

        allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
        destinationSearchEditText.clearFocus(); // Clear the focus from search bar

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
    private final TextWatcher filterTextWatcherForSearchBar = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean isTextEmpty = s.toString().equals("");
            if (!isTextEmpty) {
                //if a letter is typed

                // what should do after locating the place
                // 1 for --- search bar
                // 2 for --- source location bar
                // 3 for --- destination bar
                after_LocatingByAutocomplete_Method = 1;
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
    private final TextWatcher filterTextWatcherForSourceLocationBar = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean isTextEmpty = s.toString().equals("");
            if (!isTextEmpty) {
                //if a letter is typed

                // what should do after locating the place
                // 1 for --- search bar
                // 2 for --- source location bar
                // 3 for --- destination bar
                after_LocatingByAutocomplete_Method = 2;
                mAutoCompleteAdapter.getFilter().filter(s.toString());
            }

            /////------- ITEMS VISIBILITY  -------/////
            Map<View, Boolean> visibilityMap = new HashMap<>();

            visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
            visibilityMap.put(recyclerViewForAutocomplete, true); // Autocomplete Layout
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
    private final TextWatcher filterTextWatcherForDestinationBar = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean isTextEmpty = s.toString().equals("");
            if (!isTextEmpty) {
                //if a letter is typed

                // what should do after locating the place
                // 1 for --- search bar
                // 2 for --- source location bar
                // 3 for --- destination bar
                after_LocatingByAutocomplete_Method = 3;
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

            Log("handleAutocompleteItemClick", "Place", "Found");
            saveToRecentSearches(place.getName(), place.getLatLng());

            /////------- ITEMS VISIBILITY  -------/////
            Map<View, Boolean> visibilityMap = new HashMap<>();

            // what should do after locating the place
            // 1 for --- search bar
            // 2 for --- source location bar
            // 3 for --- destination bar

            if (after_LocatingByAutocomplete_Method == null || after_LocatingByAutocomplete_Method == 1) {

                Log("handleAutocompleteItemClick", "on", "Search Bar");

                TextInputEditText placeSearchEditText = findViewById(R.id.place_search);
                placeSearchEditText.setText(place.getName());

                DestinationLocationName = place.getName();
                DestinationLocationLatLng = place.getLatLng();

                moveCamera(place.getLatLng(), DEFAULT_ZOOM, place.getName());

                locationMarked = true;

                visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
                visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
                visibilityMap.put(DirectionsButton, true); // Directions button
                findViewById(R.id.place_search_layout).clearFocus(); // Clear the focus from search bar

            } else if (after_LocatingByAutocomplete_Method == 2) {

                Log("handleAutocompleteItemClick", "on", "Source location bar");

                TextInputEditText placeSearchEditText = findViewById(R.id.sourceLocationText);
                placeSearchEditText.setText(place.getName());

                SourceLocationName = place.getName();
                SourceLocationLatLng = place.getLatLng();

                visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
                visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
                visibilityMap.put(DirectionsButton, false); // Directions button
                findViewById(R.id.sourceLocationInputLayout).clearFocus(); // Clear the focus from search bar

            } else if (after_LocatingByAutocomplete_Method == 3) {

                Log("handleAutocompleteItemClick", "on", "Destination location bar");

                TextInputEditText placeSearchEditText = findViewById(R.id.destinationLocationText);
                placeSearchEditText.setText(place.getName());

                DestinationLocationName = place.getName();
                DestinationLocationLatLng = place.getLatLng();

                visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
                visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
                visibilityMap.put(DirectionsButton, false); // Directions button
                findViewById(R.id.destinationLocationInputLayout).clearFocus(); // Clear the focus from search bar
            }

            allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
            hideKeyboard(getCurrentFocus()); // Hide the keyboard

            // Redraw the Directions
            direction(SourceLocationLatLng, DestinationLocationLatLng);

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

        Log("showLocationOfRecentSearch", "started");

        if (after_LocatingByRecentSearch_Method == null) {
            Log("showLocationOfRecentSearch", "after_LocatingByRecentSearch_Method is NULL");
            after_LocatingByRecentSearch_Method = 1;
        }

        if (recentSearch.getLocationLatLang() != null && recentSearch.getLocationName() != null && !(recentSearch.getLocationName().isEmpty())) {

            String locationName = recentSearch.getLocationName();
            LatLng locationLatLng = recentSearch.getLocationLatLang();

            saveToRecentSearches(locationName, locationLatLng);

            moveCamera(locationLatLng, DEFAULT_ZOOM, locationName);

            Log("showLocationOfRecentSearch", "Recently searched place located on the map");

            // what should do after locating the place
            // 1 for --- search bar
            // 2 for --- source location bar
            // 3 for --- destination bar
            if (after_LocatingByRecentSearch_Method == 1) {
                Log("showLocationOfRecentSearch", "after_LocatingByRecentSearch_Method is 1");

                // Change the edit text
                TextInputEditText placeSearchEditText = findViewById(R.id.place_search);
                placeSearchEditText.setText(locationName);
                DestinationLocationName = locationName;
                DestinationLocationLatLng = locationLatLng;
                // Show the Directions button
                locationMarked = true;

            } else if (after_LocatingByRecentSearch_Method == 2) {
                Log("showLocationOfRecentSearch", "after_LocatingByRecentSearch_Method is 2");
                // Change the edit text
                TextInputEditText placeSearchEditText = findViewById(R.id.sourceLocationText);
                placeSearchEditText.setText(locationName);
                SourceLocationName = locationName;
                SourceLocationLatLng = locationLatLng;
                // Redraw the Directions
                direction(SourceLocationLatLng, DestinationLocationLatLng);

            } else if (after_LocatingByRecentSearch_Method == 3) {
                Log("showLocationOfRecentSearch", "after_LocatingByRecentSearch_Method is 3");
                // Change the edit text
                TextInputEditText placeSearchEditText = findViewById(R.id.destinationLocationText);
                placeSearchEditText.setText(locationName);
                DestinationLocationName = locationName;
                DestinationLocationLatLng = locationLatLng;
                // Redraw the Directions
                direction(SourceLocationLatLng, DestinationLocationLatLng);
            }

        } else {
            Toast.makeText(MainActivity.this, "Unable to get recent location", Toast.LENGTH_SHORT).show();
            Log("showLocationOfRecentSearch", "ERROR", "unable to get recent location");
        }

        /////------- ITEMS VISIBILITY  -------/////
        Map<View, Boolean> visibilityMap = new HashMap<>();

        // 1 for --- search bar
        // 2 for --- source location bar
        // 3 for --- destination bar
        if (after_LocatingByRecentSearch_Method == 1) {
            visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
            visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
            visibilityMap.put(DirectionsButton, true); // Directions button
            findViewById(R.id.place_search_layout).clearFocus(); // Clear the focus from search bar

        } else {
            visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
            visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout
            visibilityMap.put(DirectionsButton, false); // Directions button
            findViewById(R.id.sourceLocationInputLayout).clearFocus(); // Clear the focus from search bar
            findViewById(R.id.destinationLocationInputLayout).clearFocus(); // Clear the focus from search bar
        }

        allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map

        hideKeyboard(recentSearchLayout); // Hide the keyboard

    }


    // ------------- Show a location on map ------------- //

    private void setMapBoundary() {
//        //Limit the visible region to Sri Lanka 's bounds
//        LatLngBounds sriLankaBounds = new LatLngBounds(
//                new LatLng(5.925, 79.650),  // Southwest corner of Sri Lanka
//                new LatLng(9.825, 81.850)   // Northeast corner of Sri Lanka
//        );
//
//        // Set the padding as needed
//        int padding = 100;
//
//        // Adjust the camera position to fit the bounded region with padding
//        myMap.moveCamera(CameraUpdateFactory.newLatLngBounds(sriLankaBounds, padding));

        // Optionally, you can set a maximum zoom level to prevent the user from zooming out too far
        // myMap.setMaxZoomPreference(15.0f);  // You can adjust the value as needed


        // Move the camera to a specific location

        // Set a boundary to start
        double bottomBoundary = 6.716442;
        double leftBoundary = 79.812097;
        double topBoundary = 6.980218;
        double rightBoundary = 80.224367;

        LatLngBounds colomboBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );

        myMap.moveCamera(CameraUpdateFactory.newLatLngBounds(colomboBoundary, 100));


        // Create the main polygon (outer boundary)
        PolygonOptions polygonOptions = new PolygonOptions()
                .add(new LatLng(9.849550, 79.530713),
                        new LatLng(5.648529, 79.530713),
                        new LatLng(5.648529, 82.079541),
                        new LatLng(9.849550, 82.079541))
                .strokeColor(Color.argb(100, 128, 128, 228))  // Border color
                .fillColor(Color.argb(75, 128, 128, 128));  // Fill color

        // Create a hole within the main polygon
        List<LatLng> hole = new ArrayList<>();
        hole.add(new LatLng(6.980218, 80.224367));  // North-East corner
        hole.add(new LatLng(6.716442, 80.224367));    // South-East corner
        hole.add(new LatLng(6.716442, 79.812097));     // South-West corner
        hole.add(new LatLng(6.980218, 79.812097));  // North-West corner


        polygonOptions.addHole(hole);

        // Add the polygon to the map
        Polygon polygon = myMap.addPolygon(polygonOptions);
    }

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
                                CurrentLocationLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                SourceLocationName = "Your current location";
                                SourceLocationLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                                // Did not use moveCamera method here since it will mark the current location by a marker
                                //myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                //       DEFAULT_ZOOM));

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
        if (SourceLocationLatLng == null || SourceLocationName == null) {
            Log("showDirections", "Source location is null", "getDeviceLocation called");
            // Show current location
            getDeviceLocation();

        }

        if (SourceLocationLatLng != null
                && DestinationLocationLatLng != null
                && SourceLocationName != null
                && DestinationLocationName != null) {

            // set Location bars
            EditText SourceLocation = findViewById(R.id.sourceLocationText);
            EditText DestinationLocation = findViewById(R.id.destinationLocationText);

            SourceLocation.setText(SourceLocationName);
            Log("direction", "SourceLocation text changed");
            DestinationLocation.setText(DestinationLocationName);
            Log("direction", "DestinationLocation text changed");

            direction(SourceLocationLatLng, DestinationLocationLatLng);

            /////------- ITEMS VISIBILITY  -------/////
            Map<View, Boolean> visibilityMap = new HashMap<>();

            visibilityMap.put(findViewById(R.id.SearchBarRelLayout), false);  // Search bar
            visibilityMap.put(recentSearchLayout, false);  // recent search
            visibilityMap.put(recyclerViewForAutocomplete, false);  // autocomplete
            visibilityMap.put(DirectionsButton, false); // Directions button
            visibilityMap.put(findViewById(R.id.directionBar), true); // Direction Bar

            allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map
        }

    }

    public BitmapDescriptor getBitmapDescriptorWithDrawable(Drawable drawable, int circleColor, int strokeWidth, int strokeColor, int size) {
        int padding = 10; // Adjust the padding as needed
        int diameter = Math.max(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()) + strokeWidth * 2;
        Bitmap bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the circle with stroke
        Paint paintCircle = new Paint();
        paintCircle.setColor(circleColor);
        paintCircle.setStyle(Paint.Style.FILL);
        paintCircle.setAntiAlias(true);
        canvas.drawCircle(diameter / 2, diameter / 2, diameter / 2 - strokeWidth, paintCircle);

        Paint paintStroke = new Paint();
        paintStroke.setColor(strokeColor); // Adjust stroke color as needed
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setStrokeWidth(strokeWidth);
        paintStroke.setAntiAlias(true);
        canvas.drawCircle(diameter / 2, diameter / 2, diameter / 2 - strokeWidth, paintStroke);

        // Apply color filter to the drawable
        drawable.setColorFilter(strokeColor, PorterDuff.Mode.SRC_IN);

        // Draw the drawable on top of the circle
        int left = (diameter - drawable.getIntrinsicWidth()) / 2;
        int top = (diameter - drawable.getIntrinsicHeight()) / 2;
        int right = left + drawable.getIntrinsicWidth();
        int bottom = top + drawable.getIntrinsicHeight();
        drawable.setBounds(left, top, right, bottom);
        drawable.draw(canvas);

        // Resize the bitmap to the desired marker size
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);

        // Clear the color filter to avoid affecting other drawables
        drawable.setColorFilter(null);

        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

    private void direction(LatLng origin, LatLng destination) {

        Log("direction", "Started");

        // Show waiting progress bar
        toggleItemVisibility(findViewById(R.id.loadingProgressBar), true);

        // Remove any existing markers
        myMap.clear();

        // Marking START & END points

        // Create MarkerOptions objects
        MarkerOptions startOptions = new MarkerOptions().position(origin).title("Start");
        MarkerOptions endOptions = new MarkerOptions().position(destination).title("Destination");

        // Customize the marker icon
        // BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_start);
        BitmapDescriptor icon = getBitmapDescriptorWithDrawable(getResources().getDrawable(R.drawable.ic_directions_start), Color.WHITE, 5, Color.BLUE, 100);
        if (icon != null) {
            startOptions.icon(icon);
        } else {
            Log("direction", "ERROR: End point marker", "Icon is null");
        }

        // Add marker to the location
        myMap.addMarker(startOptions);
        myMap.addMarker(endOptions);

        //  getMostAvailableRoute(origin, destination);

//        // Time to look for
//        // Get tomorrow's date
//        Calendar tomorrow = Calendar.getInstance();
//        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
//
//        // Set the time to 12:00 noon
//        tomorrow.set(Calendar.HOUR_OF_DAY, 12);
//        tomorrow.set(Calendar.MINUTE, 0);
//        tomorrow.set(Calendar.SECOND, 0);
//        tomorrow.set(Calendar.MILLISECOND, 0);
//
//        // Convert the date to a Unix timestamp
//        long timestampInSeconds = tomorrow.getTimeInMillis() / 1000;

        long timestampInSeconds = DateUtils.getTomorrowNoonTimestamp();

        // Construct the URL for the Google Directions API request
        String apiKey = "AIzaSyDDTamV9IieqbDXoWKxjEHmBo7jRcNuhFg"; // Replace with your actual API key
        String url = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=transit" +  // Specify transit mode (bus)
                "&departure_time=" + timestampInSeconds +
                "&alternatives=true" +
                "&key=" + apiKey;

        Log("Directions", "Markers", "Added");

        // Create a request to the Directions API
        JsonObjectRequest directionsRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log("Directions", "onResponse", "Started");
                        try {
                            // Parse the JSON response
                            JSONArray routes = response.getJSONArray("routes");
                            if (routes.length() > 0) {
                                Log("Directions", "onResponse", "Route(s) found");

                                // Get the first route ------ Can add alternative routes if wanted
                                JSONObject route = routes.getJSONObject(0);
                                //Log("Directions", "route", route.toString(4));

                                // Get legs array
                                JSONArray legs = route.getJSONArray("legs");

                                if (legs.length() > 0) {
                                    // Get the first leg
                                    JSONObject leg = legs.getJSONObject(0);
                                    //Log("Directions", "legs", legs.toString());

                                    // Get steps array
                                    JSONArray steps = leg.getJSONArray("steps");
                                    for (int j = 0; j < steps.length(); j++) {


                                        // Get each step
                                        JSONObject step = steps.getJSONObject(j);
                                        //Log("Directions", "steps", step.toString(4));


                                        // Extract the encoded polyline
                                        JSONObject stepPolyline = step.getJSONObject("polyline");
                                        String encodedPolyline = stepPolyline.getString("points");

                                        // Get a random color for demonstration (replace with your logic)
                                        int polylineColor = getRandomColor();

                                        // Decode the polyline and add it to the map as a Polyline with the specific color
                                        List<LatLng> decodedPolyline = decodePolyline(encodedPolyline);

//                                        for (LatLng point : decodedPolyline) {
//                                            MarkerOptions markerOptions = new MarkerOptions().position(point).icon(
//                                                    BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_start)
//                                            );
//                                            myMap.addMarker(markerOptions);
//                                        }

                                        List<PatternItem> pattern = Arrays.asList(
                                                new Dot(),
                                                //   new Gap(20),
                                                //  new Dash(30),
                                                new Gap(20));

                                        PolylineOptions polylineOptions = new PolylineOptions()
                                                .addAll(decodedPolyline)
                                                // .pattern(pattern)

                                                .width(25)
                                                .clickable(true)
                                                //  .jointType(JointType.ROUND)
                                                .color(Color.BLACK);

                                        // Check if the step has travel_mode
                                        if (step.has("travel_mode")) {
                                            String travelMode = step.getString("travel_mode");


                                            // Call method1 for WALKING
                                            if ("WALKING".equals(travelMode)) {
                                                polylineOptions.pattern(pattern);
                                                polylineOptions.color(Color.BLUE);
                                            }
                                            // Call method2 for TRANSIT
                                            else if ("TRANSIT".equals(travelMode)) {


                                                // Check if the step is a transit step
                                                if (step.has("transit_details")) {
                                                    Log("Directions", "transit details", "FOUND");
                                                    // Extract transit details
                                                    JSONObject transitDetails = step.getJSONObject("transit_details");
                                                    String routeName = transitDetails.getJSONObject("line").getString("name");
                                                    String routeNumber = transitDetails.getJSONObject("line").getString("short_name");
                                                    String numStops = transitDetails.getString("num_stops");
                                                    String arrivalStopName = transitDetails.getJSONObject("arrival_stop").getString("name");
                                                    String departureStopName = transitDetails.getJSONObject("departure_stop").getString("name");
                                                    String arrivalStopTime = transitDetails.getJSONObject("arrival_time").getString("value");
                                                    String departureStopTime = transitDetails.getJSONObject("departure_time").getString("value");

                                                    // Display bus details as needed
//                                                    Log("Bus Details", "Route Name: ", routeName);
//                                                    Log("Bus Details", "Route Number: ", routeNumber);
//                                                    Log("Bus Details", "Number of Stops: ", numStops);
//                                                    Log("Bus Details", "Arrival Stop Name: ", arrivalStopName);
//                                                    Log("Bus Details", "Arrival Stop Time: ", arrivalStopTime);
//                                                    Log("Bus Details", "Departure Stop Name: ", departureStopName);
//                                                    Log("Bus Details", "Departure Stop Time: ", departureStopTime);

                                                    TicketModel newTicket = new TicketModel(routeName, routeNumber, arrivalStopName, arrivalStopTime, departureStopName, departureStopTime);

                                                    Log("Bus Details", "New Ticket Created: ", newTicket.toString());

                                                    ticketList.add(newTicket);

                                                    // show available busses on the route
                                                /* ------ these should added only if the busses arrive at the intersections
                                                            after the person gets there.
                                                   1. check route id
                                                   2. check the direction
                                                   3. check the arrival time to the bus stop
                                                            but for now add them all.
                                                */
                                                    getBussesOnRoute(routeNumber);
                                                    Log("direction", "onResponse", "showBussesOnRoute");
                                                }


                                                polylineOptions.addSpan(new StyleSpan(Color.RED))
                                                        .addSpan(new StyleSpan(Color.rgb(3, 161, 14)));
                                            }
                                        }

                                        myMap.addPolyline(polylineOptions);

                                        directionRoutePolyline = myMap.addPolyline(polylineOptions);
                                    }
                                }

                                Log("direction", "onResponse", "directionRoutePolyline done");

                                locationMarked = false;

                                // After adding the polyline, adjust the camera to fit the entire route
                                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                                String encodedPolyline = overviewPolyline.getString("points");
                                List<LatLng> decodedPolyline = decodePolyline(encodedPolyline);

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng point : decodedPolyline) {
                                    builder.include(point);
                                }

                                LatLngBounds bounds = builder.build();
                                int padding = 100; // Adjust this padding as needed

                                // Animate the camera to fit the entire route with padding
                                myMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

                                // Hide waiting progress bar
                                toggleItemVisibility(findViewById(R.id.loadingProgressBar), false);

                            } else {
                                Log("Directions", "onResponse", "No routes found.");
                                Toast.makeText(MainActivity.this, "No routes found.", Toast.LENGTH_SHORT).show();
                                // Hide waiting progress bar
                                toggleItemVisibility(findViewById(R.id.loadingProgressBar), false);
                            }
                        } catch (JSONException e) {
                            // Handle the exception, log an error, or take appropriate action
                            Log("Bus Details", "Error parsing JSON", e.getStackTrace().toString());
                            e.printStackTrace();
                            // Hide waiting progress bar
                            toggleItemVisibility(findViewById(R.id.loadingProgressBar), false);
                            throw new RuntimeException(e);

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log("Directions", "Volley ERROR");
                        Log("Directions", "Volley ERROR - message", error.getMessage());
                        Log("Directions", "Volley ERROR - Stack Trace", error.getStackTrace().toString());
                        Log("Directions", "Volley ERROR - Cause", error.getCause().toString());

                        Toast.makeText(MainActivity.this, "Error fetching directions.", Toast.LENGTH_SHORT).show();

                        // show the directions button again
                        toggleItemVisibility(findViewById(R.id.directionsButton), true);
                        // Hide waiting progress bar
                        toggleItemVisibility(findViewById(R.id.loadingProgressBar), false);
                    }
                });

        // Set a retry policy for the request
        RetryPolicy retryPolicy = new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );

        Log("Directions", "directionsRequest", "retry policy");
        directionsRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(directionsRequest);

    }

    // getMostAvailableRoute
//    private void getMostAvailableRoute(LatLng origin, LatLng destination) {
//        // Time to look for
//        // Get tomorrow's date
//        Calendar tomorrow = Calendar.getInstance();
//        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
//
//        // Set the time to 12:00 noon
//        tomorrow.set(Calendar.HOUR_OF_DAY, 12);
//        tomorrow.set(Calendar.MINUTE, 0);
//        tomorrow.set(Calendar.SECOND, 0);
//        tomorrow.set(Calendar.MILLISECOND, 0);
//
//        // Convert the date to a Unix timestamp
//        long timestampInSeconds = tomorrow.getTimeInMillis() / 1000;
//
//        // Construct the URL for the Google Directions API request
//        String apiKey = "AIzaSyDDTamV9IieqbDXoWKxjEHmBo7jRcNuhFg"; // Replace with your actual API key
//        String url = "https://maps.googleapis.com/maps/api/directions/json" +
//                "?origin=" + origin.latitude + "," + origin.longitude +
//                "&destination=" + destination.latitude + "," + destination.longitude +
//                "&mode=transit" +  // Specify transit mode (bus)
//                "&departure_time=" + timestampInSeconds +
//            //    "&alternatives=true" +
//                "&key=" + apiKey;
//
//        Log("Directions", "Markers", "Added");
//
//        // Create a request to the Directions API
//        JsonObjectRequest directionsRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        Log("Directions", "onResponse", "Started");
//                        try {
//                            // Parse the JSON response
//                            JSONArray routes = response.getJSONArray("routes");
//                            if (routes.length() > 0) {
//                                Log("Directions", "onResponse", "Route(s) found");
//
//                                // Process each route
//                                for (int j = 0; j < routes.length(); j++) {
//                                    JSONObject route = routes.getJSONObject(j);
//
//                                    JSONArray legs = route.getJSONArray("legs");
//                                    if (legs.length() > 0) {
//                                        JSONObject leg = legs.getJSONObject(0);
//                                        JSONArray steps = leg.getJSONArray("steps");
//
//                                        // Process each step
//                                        for (int k = 0; k < steps.length(); k++) {
//                                            JSONObject step = steps.getJSONObject(k);
//
//
//                                            // Extract the encoded polyline
//                                            JSONObject stepPolyline = step.getJSONObject("polyline");
//                                            String encodedPolyline = stepPolyline.getString("points");
//
//                                            // Decode the polyline and add it to the map as a Polyline with the specific color
//                                            List<LatLng> decodedPolyline = decodePolyline(encodedPolyline);
//
//                                            List<PatternItem> pattern = Arrays.asList(
//                                                    new Dot(),
//                                                    //   new Gap(20),
//                                                    //  new Dash(30),
//                                                    new Gap(20));
//
//                                            PolylineOptions polylineOptions = new PolylineOptions()
//                                                    .addAll(decodedPolyline)
//                                                    // .pattern(pattern)
//
//                                                    .width(25)
//                                                    .clickable(true)
//                                                    //  .jointType(JointType.ROUND)
//                                                    .color(Color.BLACK);
//
//                                            // Check if the step has travel_mode
//                                            if (step.has("travel_mode")) {
//                                                String travelMode = step.getString("travel_mode");
//
//
//                                                // Call method1 for WALKING
//                                                if ("WALKING".equals(travelMode)) {
//                                                    polylineOptions.pattern(pattern);
//                                                    polylineOptions.color(Color.BLUE);
//                                                }
//                                                // Call method2 for TRANSIT
//                                                else if ("TRANSIT".equals(travelMode)) {
//
//
//                                                    // Check if the step is a transit step
//                                                    if (step.has("transit_details")) {
//                                                        Log("Directions", "transit details", "FOUND");
//                                                        // Extract transit details
//                                                        JSONObject transitDetails = step.getJSONObject("transit_details");
//                                                        String routeName = transitDetails.getJSONObject("line").getString("name");
//                                                        String routeNumber = transitDetails.getJSONObject("line").getString("short_name");
//                                                        String numStops = transitDetails.getString("num_stops");
//                                                        String arrivalStop = transitDetails.getJSONObject("arrival_stop").getString("name");
//
//                                                        // Display bus details as needed
//                                                        Log("Bus Details", "Route Name: ", routeName);
//                                                        Log("Bus Details", "Route Number: ", routeNumber);
//                                                        Log("Bus Details", "Number of Stops: ", numStops);
//                                                        Log("Bus Details", "Arrival Stop: ", arrivalStop);
//
//                                                        // show available busses on the route
//                                                /* ------ these should added only if the busses arrive at the intersections
//                                                            after the person gets there.
//                                                   1. check route id
//                                                   2. check the direction
//                                                   3. check the arrival time to the bus stop
//                                                            but for now add them all.
//                                                */
//                                                        getBussesOnRoute(routeNumber);
//                                                        Log("direction", "onResponse", "showBussesOnRoute");
//                                                    }
//
//
//                                                    polylineOptions.addSpan(new StyleSpan(Color.RED))
//                                                            .addSpan(new StyleSpan(Color.rgb(3, 161, 14)));
//                                                }
//                                            }
//
//                                            myMap.addPolyline(polylineOptions);
//
//                                            directionRoutePolyline = myMap.addPolyline(polylineOptions);
//                                        }
//                                    }
//
//                                    Log("direction", "onResponse", "directionRoutePolyline done");
//
//                                    locationMarked = false;
//
//                                    // After adding the polyline, adjust the camera to fit the entire route
//                                    JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
//                                    String encodedPolyline = overviewPolyline.getString("points");
//                                    List<LatLng> decodedPolyline = decodePolyline(encodedPolyline);
//
//                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                                    for (LatLng point : decodedPolyline) {
//                                        builder.include(point);
//                                    }
//
//                                    LatLngBounds bounds = builder.build();
//                                    int padding = 100; // Adjust this padding as needed
//
//                                    // Animate the camera to fit the entire route with padding
//                                    myMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
//
//                                    // Hide waiting progress bar
//                                    toggleItemVisibility(findViewById(R.id.loadingProgressBar), false);
//                                }
//
//                            } else {
//                                Log("Directions", "onResponse", "No routes found.");
//                                Toast.makeText(MainActivity.this, "No routes found.", Toast.LENGTH_SHORT).show();
//                                // Hide waiting progress bar
//                                toggleItemVisibility(findViewById(R.id.loadingProgressBar), false);
//                            }
//                        } catch (JSONException e) {
//                            // Handle the exception, log an error, or take appropriate action
//                            Log("Bus Details", "Error parsing JSON", e.getStackTrace().toString());
//                            e.printStackTrace();
//                            // Hide waiting progress bar
//                            toggleItemVisibility(findViewById(R.id.loadingProgressBar), false);
//                            throw new RuntimeException(e);
//
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log("Directions", "Volley ERROR");
//                        Log("Directions", "Volley ERROR - message", error.getMessage());
//                        Log("Directions", "Volley ERROR - Stack Trace", error.getStackTrace().toString());
//                        Log("Directions", "Volley ERROR - Cause", error.getCause().toString());
//
//                        Toast.makeText(MainActivity.this, "Error fetching directions.", Toast.LENGTH_SHORT).show();
//
//                        // show the directions button again
//                        toggleItemVisibility(findViewById(R.id.directionsButton), true);
//                        // Hide waiting progress bar
//                        toggleItemVisibility(findViewById(R.id.loadingProgressBar), false);
//                    }
//                });
//
//        // Set a retry policy for the request
//        RetryPolicy retryPolicy = new DefaultRetryPolicy(
//                0,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        );
//
//        Log("Directions", "directionsRequest", "retry policy");
//        directionsRequest.setRetryPolicy(retryPolicy);
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(directionsRequest);
//    }

    private int getRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

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

    private List<LatLng> decodePolylineForWalking(String encoded) {
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
        //clearBusMapMarkers();
        clearBusMapMarkers();

        EditText SourceLocation = findViewById(R.id.sourceLocationText);
        EditText DestinationLocation = findViewById(R.id.destinationLocationText);
        Editable temp;
        LatLng tempLatLng;

        Log("swapLocations", "Before SourceLocationLatLng", SourceLocationLatLng.toString());
        Log("swapLocations", "Before DestinationLocationLatLng", DestinationLocationLatLng.toString());

        if (SourceLocation.getText() != null && DestinationLocation.getText() != null) {
            temp = SourceLocation.getText();
            SourceLocation.setText(DestinationLocation.getText());
            SourceLocationName = DestinationLocationName;
            DestinationLocation.setText(temp);
            DestinationLocationName = temp.toString();

            tempLatLng = SourceLocationLatLng;
            SourceLocationLatLng = DestinationLocationLatLng;
            DestinationLocationLatLng = tempLatLng;


            Log("swapLocations", "SourceLocation.getText().toString()", SourceLocation.getText().toString());
            Log("swapLocations", "DestinationLocation.getText().toString()", DestinationLocation.getText().toString());

            direction(SourceLocationLatLng, DestinationLocationLatLng);
        }

        /////------- ITEMS VISIBILITY  -------/////
        Map<View, Boolean> visibilityMap = new HashMap<>();

        visibilityMap.put(recentSearchLayout, false); // Recent Search Layout
        visibilityMap.put(recyclerViewForAutocomplete, false); // Autocomplete Layout

        allItemVisibilitySwitcher(visibilityMap); // Toggle visibility based on the map


    }

    private void getBussesOnRoute(String routeNumber) {

        Log("getBussesOnRoute", "Started");

        // Trigger the request to fetch bus locations
        busLocationViewModel.getBusLocations(routeNumber);

        // Observe LiveData
        busLocationViewModel.getBusLocationsLiveData().observe(this, availableBusses -> {
            Log("getBussesOnRoute", "Observe LiveData", availableBusses.toString());
            try {
                if (!availableBusses.isEmpty()) {
                    for (BusLocationModel bus : availableBusses) {
                        Log("getBussesOnRoute", "bus", bus.toString());

                        // Check if the bus ID is already added
                        if (!availableBusIds.contains(bus.getBus().getBusId())) {
                            Log("getBussesOnRoute", "busLocations", availableBusList.toString());
                            Log("getBussesOnRoute", "bus is added to the list");

                            // getFarePrice(routeNumber, String startBusStop, String endBusStop)


                            // Add new buses to the arrays
                            // availableBusListDetails.add(bus);
                            availableBusList.add(bus);
                            availableBusIds.add(bus.getBus().getBusId());
                        } else {
                            Log("getBussesOnRoute", "bus is already on the list");
                        }
                    }
                } else {
                    Log("getBussesOnRoute", "No busses in the response");
                }
            } catch (Exception e) {
                Log("getBussesOnRoute", "ERROR", e.getMessage());
                Toast.makeText(this, "Network error: Unable to connect to the server.", Toast.LENGTH_SHORT).show();
                //throw new RuntimeException(e);
            }

            // Update UI with bus locations --- if these called in the getBusLocationsLiveData, it will go in a feedback cycle
            if (availableBusIds != null) {
                addMapMarkers();
                updateBusMarkers();
            }


        });


        busLocationViewModel.getErrorLiveData().observe(this, errorMessage -> {
            Log("getBussesOnRoute", "Handle errors", errorMessage);
        });
    }

    private void getFarePrice(String routeId, String startBusStop, String endBusStop) {

        Log("getFarePrice", "Started");

        // Trigger the request to fetch bus locations
        ticketViewModel.getFarePrice(routeId, startBusStop, endBusStop);

        // Observe LiveData
        ticketViewModel.getFarePriceLiveData().observe(this, fare -> {
            Log("getFarePrice", "Observe LiveData", String.valueOf(fare));
            try {
                Log("getFarePrice", "fare price received");

            } catch (Exception e) {
                Log("getFarePrice", "Could not get the fare price", e.getMessage());
            }
        });

        ticketViewModel.getErrorLiveData().observe(this, errorMessage -> {
            Log("getFarePrice", "Handle errors", errorMessage);
        });
    }


//    private void hasBusPassedBusStop(String routeNumber) {
//
//        Log("getBussesOnRoute", "Started");
//
//        // Trigger the request to fetch bus locations
//        busLocationViewModel.getBusLocations(routeNumber);
//
//        // Observe LiveData
//        busLocationViewModel.getBusLocationsLiveData().observe(this, availableBusses -> {
//            Log("getBussesOnRoute", "Observe LiveData", availableBusses.toString());
//            try {
//                if (!availableBusses.isEmpty()) {
//                    for (BusModel bus : availableBusses) {
//                        Log("getBussesOnRoute", "bus", bus.toString());
//
//                        // Check if the bus ID is already added
//                        if (!availableBusIds.contains(bus.getBusId())) {
//                            Log("getBussesOnRoute", "busLocations", availableBusList.toString());
//                            Log("getBussesOnRoute", "bus is added to the list");
//
//                            // Add new buses to the arrays
//                            availableBusList.add(bus);
//                            availableBusIds.add(bus.getBusId());
//                        } else {
//                            Log("getBussesOnRoute", "bus is already on the list");
//                        }
//                    }
//                } else {
//                    Log("getBussesOnRoute", "No busses in the response");
//                }
//            } catch (Exception e) {
//                Log("getBussesOnRoute", "ERROR", e.getMessage());
//                Toast.makeText(this, "Network error: Unable to connect to the server.", Toast.LENGTH_SHORT).show();
//                //throw new RuntimeException(e);
//            }
//
//            // Update UI with bus locations --- if these called in the getBusLocationsLiveData, it will go in a feedback cycle
//            if (availableBusIds != null){
//                addMapMarkers();
//                updateBusMarkers();
//            }
//
//
//        });
//
//
//        busLocationViewModel.getErrorLiveData().observe(this, errorMessage -> {
//            Log("getBussesOnRoute", "Handle errors", errorMessage);
//        });
//    }

    private void addMapMarkers() {

        Log("addMapMarkers", "Started");
        if (myMap != null) {

            // Loop through the bus locations
            Log("addMapMarkers", "busLocation", availableBusList.toString());
            for (BusLocationModel busLocation : availableBusList) {
                if (busLocation.getLocation() != null) {
                    Log("addMapMarkers", "location", busLocation.getLocation().toString());
                    try {
                        String title = "";
                        if (busLocation.getBus().getBusNumber() == null || busLocation.getBus().getBusNumber().isEmpty()) {
                            title = "Bus number: error";
                        } else {
                            title = "Bus number: " + busLocation.getBus().getBusNumber();
                        }

                        String snippet = "";
                        if (busLocation.getBus().getBusNumber() == null || busLocation.getBus().getBusNumber().isEmpty()) {
                            snippet = "Route: error";
                        } else {
                            // TODO: 2023-12-20 add Estimated arrival:
                            snippet = "Route: " + busLocation.getBus().getRouteId();
                        }


                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(busLocation.getLocation().latitude, busLocation.getLocation().longitude))
                                .title(title)
                                .snippet(snippet);

                        // Customize the marker icon
                        int intColor;
                        try {
                            intColor = Color.parseColor(busLocation.getBus().getBusColor());
                        } catch (Exception e) {
                            Log("addMapMarkers", "ERROR", "Parsing color " + e.getMessage());
                            // assign BLACK
                            intColor = -16777216;
                        }

                        // BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_marker);
                        BitmapDescriptor icon = getBitmapDescriptorWithDrawable(getResources().getDrawable(R.drawable.ic_bus_marker), Color.WHITE, 5, intColor, 100);

                        if (icon != null) {
                            markerOptions.icon(icon);
                        } else {
                            Log("addMapMarkers", "ERROR: bus point marker", "Icon is null");
                        }

                        // Add marker to the map and update the busMarkers map
                        Marker newMarker = myMap.addMarker(markerOptions);
                        String markerId = busLocation.getBus().getBusId();
                        if (newMarker != null) {
                            newMarker.setTag(markerId);
                        }

                        busMarkers.put(busLocation.getBus().getBusId(), newMarker);
                        Log("addNewBusMarker", "marker added");


                    } catch (NullPointerException e) {
                        Log("addMapMarkers", "NullPointerException", e.getMessage());
                    }
                }

            }
            //    setCameraView();
        }
    }

    private void updateBusMarkers() {
        // Method to  get updated bus locations and update markers every 5 seconds
        Log("updateBusLocationsAndMarkers", "Started");
        // Check if availableBusIds set is not empty
        if (!availableBusIds.isEmpty()) {
            // Call the ViewModel method to update bus locations for the available busIds
            Log("updateBusLocationsAndMarkers", "updateBusLocations called", availableBusIds.toString());

            // TO GET THE ROUTE ID AND BUSSTOP ID FOR THE HASBUSPASSED FUNCTION

            busLocationViewModel.updateBusLocations(availableBusIds
                    //   , routeId, busStopId
            );

            // Observe LiveData to get updated bus locations
            busLocationViewModel.getUpdatedBusLocationsLiveData().observe(this, updatedBusLocations -> {
                // Update markers on the map with the new bus locations
                Log("updateBusLocationsAndMarkers", "observing bus location live data");
                updateMarkerPositions(updatedBusLocations);
            });

            // Observe LiveData for errors
            busLocationViewModel.getErrorLiveData().observe(this, errorMessage -> {
                // Handle errors, e.g., display an error message
                Log("updateBusMarkers", "Error: ", errorMessage);
            });
        } else {
            Log("updateBusMarkers", "Available bus IDs set is empty");
        }
    }

    private void updateMarkerPositions(List<BusLocationModel> updatedBusLocations) {
        try {
            Log("updateMarkerPositions", "Started");

            // Check if the availableBusIds set is not empty
            if (availableBusIds.isEmpty()) {
                Log("updateMarkerPositions", "Available bus IDs set is empty");
                return; // No need to proceed if the set is empty
            }

            // Iterate through the list of updated bus locations
            for (BusLocationModel updatedBus : updatedBusLocations) {
                // Check if the updatedBus is in the availableBusIds set
                if (availableBusIds.contains(updatedBus.getBus().getBusId())) {
                    // Check if the bus marker is already on the map
                    if (busMarkers.containsKey(updatedBus.getBus().getBusId())) {
                        Marker marker = busMarkers.get(updatedBus.getBus().getBusId());
                        if (marker != null) {
                            // Update the marker position on the map
                            LatLng newLatLng = new LatLng(updatedBus.getLocation().latitude, updatedBus.getLocation().longitude);
                            marker.setPosition(newLatLng);
                        }
                    } else {
                        // Handle the case where the bus marker is not on the map
                        Log("updateMarkerPositions", " Bus marker not found on the map for bus ID " + updatedBus.getBus().getBusId());
                    }
                } else {
                    Log("updateMarkerPositions", "Bus ID" + updatedBus.getBus().getBusId() + " not in available bus IDs set");
                }
            }
        } catch (Exception e) {
            Log("updateMarkerPositions", "Error", e.getMessage());
        }
    }

    private BusLocationModel findBusById(String busId) {
        for (BusLocationModel bus : availableBusList) {
            if (bus.getBus().getBusId().equals(busId)) {
                return bus;
            }
        }
        return null; // Not found
    }

    private TicketModel findTicketByRouteNumber(String routeNumber) {
        for (TicketModel ticket : ticketList) {
            if (ticket.getRouteNumber().equals(routeNumber)) {
                return ticket;
            }
        }
        return null; // Not found
    }

    private void showBusDetails(BusLocationModel bus) {

        FullWidthDialog dialog = new FullWidthDialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(R.layout.bus_details_layout);

        ImageView busDetails_Image = dialog.findViewById(R.id.busDetails_Image);
        TextView busDetails_route = dialog.findViewById(R.id.busDetails_Route);
        TextView busDetails_VehicleNumber = dialog.findViewById(R.id.busDetails_VehicleNumber);
        TextView busDetails_GetOnPlace = dialog.findViewById(R.id.busDetails_GetOnPlace);
        TextView busDetails_GetOnTime = dialog.findViewById(R.id.busDetails_GetOnTime);
        TextView busDetails_GetDownPlace = dialog.findViewById(R.id.busDetails_GetDownPlace);
        TextView busDetails_GetDownTime = dialog.findViewById(R.id.busDetails_GetDownTime);
        TextView busDetails_TicketPrice = dialog.findViewById(R.id.busDetails_TicketPrice);
        ImageButton busDetails_CloseButton = dialog.findViewById(R.id.busDetails_CloseButton);
        Button busDetails_purchaseTicketButton = dialog.findViewById(R.id.purchaseTicketButton);



        // Initiate the views
       // RelativeLayout busDetails_RelativeLayout = findViewById(R.id.busDetails_RelativeLayout);
//        ImageView busDetails_Image = findViewById(R.id.busDetails_Image);
//        TextView busDetails_route = findViewById(R.id.busDetails_Route);
//        TextView busDetails_VehicleNumber = findViewById(R.id.busDetails_VehicleNumber);
//        TextView busDetails_GetOnPlace = findViewById(R.id.busDetails_GetOnPlace);
//        TextView busDetails_GetOnTime = findViewById(R.id.busDetails_GetOnTime);
//        TextView busDetails_GetDownPlace = findViewById(R.id.busDetails_GetDownPlace);
//        TextView busDetails_GetDownTime = findViewById(R.id.busDetails_GetDownTime);
//        TextView busDetails_TicketPrice = findViewById(R.id.busDetails_TicketPrice);
//        ImageButton busDetails_CloseButton = findViewById(R.id.busDetails_CloseButton);
//        Button busDetails_purchaseTicketButton = findViewById(R.id.purchaseTicketButton);

        // Get the Ticket details
        TicketModel ticket = findTicketByRouteNumber(bus.getBus().getRouteId());
        if (ticket != null) {
            Log("showBusDetails", "A valid ticket found");

            // Assign the details to views
            busDetails_route.setText(ticket.getRouteNumber() + " | " + ticket.getRouteName());
            busDetails_VehicleNumber.setText(bus.getBus().getBusNumber());
            busDetails_GetOnPlace.setText(ticket.getArrivalStopName());
            busDetails_GetDownPlace.setText(ticket.getDepartureStopName());

            // TODO: 2023-12-23 THESE NEEDS TO BE CHANGED TO REAL TIME THAT A BUS WILL COME
            long arrivalTimeInMillis = Long.parseLong(ticket.getArrivalStopTime()) * 1000L;
            busDetails_GetOnTime.setText(timestampToFormattedTime(arrivalTimeInMillis));
            long departureTimeInMillis = Long.parseLong(ticket.getDepartureStopTime()) * 1000L;
            busDetails_GetDownTime.setText(timestampToFormattedTime(departureTimeInMillis));

            // Trigger the request to fetch fare price
            ticketViewModel.getFarePrice(ticket.getRouteNumber(), ticket.getArrivalStopName(), ticket.getDepartureStopName());

            // Observe LiveData for fare price
            ticketViewModel.getFarePriceLiveData().observe(this, fare -> {
                try {
                    if (fare != null) {

                        // Format the double with 2 decimal places
                        String formattedFare = String.format("%.2f", fare);

                        busDetails_TicketPrice.setText("Rs." + formattedFare);
                        ticket.setFarePrice(fare);

                        busDetails_purchaseTicketButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Create an Intent to navigate to the new activity
                                Intent intent = new Intent(MainActivity.this, PurchaseTicket.class);

                                // Pass the Ticket object to the next activity
                                intent.putExtra("ticket", ticket);
                                dialog.dismiss();
                                // Start the new activity
                                startActivity(intent);
                            }
                        });


                    } else {
                        busDetails_TicketPrice.setText("(Calculating...)");
                        // TODO: Make the purchase button inactive
                    }
                } catch (Exception e) {
                    Log("showBusDetails", "Could not update the fare price", e.getMessage());
                }
            });

            busDetails_CloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //toggleItemVisibility(busDetails_RelativeLayout, false);
                    dialog.dismiss();
                }
            });

            // Set dialog position to the bottom
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.gravity = Gravity.BOTTOM;
           // params.y = 100;  // Adjust the y position as needed

            // Apply the updated attributes
            dialog.getWindow().setAttributes(params);

            // Get the root view of the activity
            ViewGroup rootView = getWindow().getDecorView().findViewById(android.R.id.content);

            // Show a semi-transparent overlay
            View overlay = new View(MainActivity.this);
            overlay.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            overlay.setBackgroundColor(Color.parseColor("#80000000")); // #80 for 50% alpha. #BF 75%

            // Add the overlay to the root view
            rootView.addView(overlay);

            // Set an OnDismissListener to remove the overlay when the dialog dismisses
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    rootView.removeView(overlay);
                }
            });

            dialog.show();
          //  toggleItemVisibility(busDetails_RelativeLayout, true);
        } else {
            Toast.makeText(this, "Sorry! Bus details not found", Toast.LENGTH_SHORT).show();
            Log("showBusDetails", "ERROR", "Cannot find the ticket");

        }



    }

//    private void setCameraView() {
//
//        // Set a boundary to start
//        double bottomBoundary = busLocation.getLocation().latitude - .1;
//        double leftBoundary = busLocation.getLocation().longitude - .1;
//        double topBoundary = busLocation.getLocation().latitude + .1;
//        double rightBoundary = busLocation.getLocation().longitude + .1;
//
//        mapBoundarySL = new LatLngBounds(
//                new LatLng(bottomBoundary, leftBoundary),
//                new LatLng(topBoundary, rightBoundary)
//        );
//
//        myMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBoundarySL, 0));
//    }

    private void clearBusMapMarkers() {
        // Stop updating bus locations by removing the observer
        if (busLocationViewModel != null) {
            busLocationViewModel.getBusLocationsLiveData().removeObservers(this);
            busLocationViewModel.getUpdatedBusLocationsLiveData().removeObservers(this);
            // Reset data in the ViewModel and Repository
            busLocationViewModel.resetData();
        }

        // Clear the bus locations list
        if (availableBusList != null) {
            availableBusList.clear();
        }

        if (availableBusIds != null) {
            availableBusIds.clear();
        }

        // myMap.clear(); will remove all the objects on the map, including markers, polylines, and other overlays from the map
        if (myMap != null) {
            for (Marker marker : busMarkers.values()) {
                marker.remove();
            }
            busMarkers.clear();
        }

        if (busMarkers != null) {
            busMarkers.clear();
        }


        toggleItemVisibility(findViewById(R.id.busDetails_RelativeLayout), false);

        Log("clearBusMapMarkers", "bus location observation stopped");
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


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log("onMarkerClick", "Marker called");
        marker.showInfoWindow();
        return true;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Log("getInfoWindow", "getInfoWindow called");
        return null; // Use default info window
    }

    @Override
    public View getInfoContents(Marker marker) {
        Log("getInfoContents", "getInfoContents called");
        View view = LayoutInflater.from(this).inflate(R.layout.custom_info_window, null);

        TextView titleTextView = view.findViewById(R.id.vehicleNumberTextView);
        TextView snippetTextView = view.findViewById(R.id.routeInfoTextView);

        titleTextView.setText(marker.getTitle());
        snippetTextView.setText(marker.getSnippet());

        Button purchaseTicketButton = view.findViewById(R.id.purchaseTicketButton);
        purchaseTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve starting and ending bus stops (replace with your data)
                String startingStop = "Bus Stop A";
                String endingStop = "Bus Stop B";
                Log("getInfoContents", "purchaseTicketButton clicked");
                // Create an intent to start the PurchaseTicketActivity
                Intent intent = new Intent(MainActivity.this, virtualTicket.class);
                intent.putExtra("startingStop", startingStop);
                intent.putExtra("endingStop", endingStop);
                startActivity(intent);
            }
        });

        return view;
    }


}


// TODO: 2023-12-07  MAKE FONT STYLE instead of using bold etc
