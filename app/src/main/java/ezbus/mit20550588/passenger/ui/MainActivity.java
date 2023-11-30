package ezbus.mit20550588.passenger.ui;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TableRow;
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
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.ui.Settings.Settings;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    // widgets
    private GoogleMap myMap;
    private SearchView mapSearchView;
    private ProgressBar loadingProgressBar;
    private ImageView mGps;

    // constants
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    // variables
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean isSearchInProgress = false;
    private Handler handler = new Handler();
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        isServicesOK();
        getLocationPermission();



//
//
//        // Initialize Places SDK
//        Places.initialize(getApplicationContext(), "YOUR_API_KEY");
//        placesClient = Places.createClient(this);
//
//        // Initialize AutocompleteSupportFragment
//        AutocompleteSupportFragment autocompleteFragment =
//                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_container);
//
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
//        autocompleteFragment.setHint("Search for a place");
//        autocompleteFragment.setActivityMode(AutocompleteActivityMode.OVERLAY);
//        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
//        autocompleteFragment.setCountry("SL");  // e.g., "US" for United States
//

//        autocompleteFragment.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
//            @Override
//            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
//                LatLng location = place.getLatLng();
//                String placeName = place.getName();
//                updateMap(location, placeName);
//
//                // Hide the keyboard when a place is selected
//                hideKeyboard();
//
//                return true;
//            }
//
//            @Override
//            public void onError(@NonNull Status status) {
//                Log.e(TAG, "Error: " + status.getStatusMessage());
//                Toast.makeText(MainActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });


//        View.OnClickListener listener = new View.OnClickListener() {
//            public void onClick(View view) {
//                virtualTicket();
//            }
//        };
//        findViewById(R.id.button).setOnClickListener(listener);


        // Initialize Settings Button
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

//        Button purchase = (Button) findViewById(R.id.button);
//        purchase.setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View view) { virtualTicket(); } });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");

        myMap = googleMap;

        ////////// PLACES API TASK //////////////  added according to YT CodeWithMitch

        if (mLocationPermissionsGranted) {

            Log.d(TAG, "onMapReady: checking for permissions and settings map controls");

            // Checking permissions
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onMapReady: permissions are not granted");
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

            ImageView mGps = findViewById(R.id.ic_gps);
            mGps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: clicked gps icon");
                    getDeviceLocation();
                }
            });

            // Initiate Search Bar
            initSearchBar();

            // Hide waiting progress bar
            hideProgressBar();

        }

    }


    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            Toast.makeText(this, "isServicesOK: Google Play Services is working", Toast.LENGTH_SHORT).show();
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
            Toast.makeText(this, "isServicesOK: an error occurred but we can fix it", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MainActivity.this);
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                Log.d(TAG, "found location: " + currentLocation);
                                // Did not use moveCamera method here since it will mark the current location by a marker
                                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM));
                            } else {
                                Log.d(TAG, "onComplete: current location is null");
                                Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void initSearchBar() {

        Log.d(TAG, "Search bar initiated");

        mapSearchView = findViewById(R.id.mapSearch);


        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s) && myMap != null && !isSearchInProgress) {


                    isSearchInProgress = true; // Set the flag to indicate search is in progress

                    Log.d(TAG, "Search query submitted");
                    Log.d(TAG, "Search query: " + s);
                    try {
                        searchByLocationName();

                        // Reset the flag after a short delay to prevent rapid consecutive searches
                        handler.postDelayed(() -> isSearchInProgress = false, 1000);

                    } catch (Exception e) {
                        Log.e(TAG, "Error during calling searchByLocationName() in setQueryTextListener " + e.getMessage());


                    }
//                    // Hide the keyboard when the button is clicked
//                    hideKeyboard();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    // ORIGINAL METHODS - CODE

    private void hideProgressBar() {
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        loadingProgressBar.setVisibility(View.GONE);
    }

    private void virtualTicket() {

        Intent intent = new Intent(this, virtualTicket.class);
        startActivity(intent);
    }

    public boolean searchByLocationName() {
        Log.d(TAG, "geoLocate: geolocating");

        String locationInput = mapSearchView.getQuery().toString();

        Log.d(TAG, "geoLocate: geolocating 2");

        List<Address> addressList = null;

        Geocoder geocoder = new Geocoder(MainActivity.this);

        if (locationInput != null) {


            if (TextUtils.isEmpty(locationInput)) {
                Toast.makeText(MainActivity.this, "Please enter a location name.", Toast.LENGTH_SHORT).show();
            } else {
                try {

                    Log.d(TAG, "geoLocate: geolocating 3");


                    addressList = geocoder.getFromLocationName(locationInput, 1);
                    Log.d(TAG, "geoLocate: geolocating 4");
                    Address address = addressList.get(0);
                    Log.d(TAG, "geoLocate: found a location: " + address.toString());
                    LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
                    moveCamera(latlng, DEFAULT_ZOOM, address.getAddressLine(0));

//
//                    LatLng ucsc = new LatLng(6.90246, 79.86115);
//                    LatLng bus = new LatLng(6.896667, 79.859973);
//                    addCircleToMap(bus, 50); // 500 meters radius
//
//                    // Set an OnCircleClickListener for the circle
//                    myMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
//                        @Override
//                        public void onCircleClick(Circle clickedCircle) {
//                            // Handle the circle click here
//                            if (clickedCircle.getTag() != null) {
//                                String circleTag = clickedCircle.getTag().toString();
//                                // Do something based on the circle's tag (if needed)
//                                if (circleTag.equals("Your circle's tag")) {
//                                    // Perform the desired action when the circle is clicked
//                                    // Example: Show a toast message
////                                    Toast.makeText(MainActivity.this, "Circle clicked!", Toast.LENGTH_SHORT).show();
////                                    bannerTableRow.setVisibility(View.VISIBLE); // Show the banner TableRow
//
//                                }
//                            }
//                        }
//                    });

//                    direction(ucsc, latlng);
                } catch (Exception e) {
                    Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Place not found.", Toast.LENGTH_SHORT).show();

                }
            }
        }


        if (TextUtils.isEmpty(locationInput)) {
            Toast.makeText(MainActivity.this, "Please enter a location name.", Toast.LENGTH_SHORT).show();


            return false;
        }
//
//        // Move network operations to a background thread
//        new AsyncTask<String, Void, List<Address>>() {
//            @Override
//            protected List<Address> doInBackground(String... params) {
//                String locationInput = params[0];
//                Geocoder geocoder = new Geocoder(MainActivity.this);
//                try {
//
//                    isSearchInProgress = false; // Reset the flag after the search is complete
//
//                    return geocoder.getFromLocationName(locationInput, 1);
//
//
//                } catch (@SuppressLint("StaticFieldLeak") IOException e) {
//                    Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
//                    isSearchInProgress = false; // Reset the flag after the search is complete
//                    return null;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(List<Address> addressList) {
//                if (addressList != null && !addressList.isEmpty()) {
//                    Address address = addressList.get(0);
//                    Log.d(TAG, "geoLocate: found a location: " + address.toString());
//                    LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
//                    moveCamera(latlng, DEFAULT_ZOOM);
//
//                    isSearchInProgress = false; // Reset the flag after the search is complete
//
//                    LatLng ucsc = new LatLng(6.90246, 79.86115);
//                    LatLng bus = new LatLng(6.896667, 79.859973);
//                    addCircleToMap(bus, 50); // 500 meters radius
//
//                    // Ensure UI operations are performed on the main thread
//                    runOnUiThread(() -> {
//                        // Set an OnCircleClickListener for the circle
//                        myMap.setOnCircleClickListener(clickedCircle -> {
//                            // Handle the circle click here
//                            if (clickedCircle.getTag() != null) {
//                                String circleTag = clickedCircle.getTag().toString();
//                                // Do something based on the circle's tag (if needed)
//                                if (circleTag.equals("Your circle's tag")) {
//                                    // Perform the desired action when the circle is clicked
//                                    // Example: Show a toast message
//                                    Toast.makeText(MainActivity.this, "Circle clicked!", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    });
//                } else {
//                    Toast.makeText(MainActivity.this, "Place not found.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }.execute(locationInput);


        return false;
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

    private void moveCamera(LatLng location, float zoom, String title) {
        if (myMap != null) {

            Log.d(TAG, "moveCamera: moving the camera to: lat: " + location.latitude + ", lng: " + location.longitude);

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


    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Add a method to request and display directions
    private void direction(LatLng origin, LatLng destination) {
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
                                myMap.addPolyline(polylineOptions);

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
//                            // After parsing is done, hide the progress bar
//                            hideProgressBar();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing directions.", Toast.LENGTH_SHORT).show();
                            // Hide the progress bar in case of an error
//                            hideProgressBar();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error fetching directions.", Toast.LENGTH_SHORT).show();
                        // Hide the progress bar in case of an error
//                        hideProgressBar();
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

    // Helper method to decode the polyline points
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


}

//  1
//  onCreate: Called when the activity is first created.

//  2
//  isServicesOK: Checks Google Play Services availability.

//  3
//  getLocationPermission: Requests and checks location permissions.

//  4
//  onRequestPermissionsResult

//  5
//  initMap: Initializes the map.

//  6
//  onMapReady: Called when the map is ready.

//  7
//  getDeviceLocation()

//  8
//  moveCamera

//  9


//  showProgressBar: Displays the progress bar.
//  7
//  hideProgressBar: Hides the progress bar.
//  8
//  virtualTicket: Opens the virtual ticket activity.
//  9
//  searchByLocationName: Searches for a location by name and updates the map.
//  10
//  isEmpty: Checks if an EditText is empty.
//  11
//  addCircleToMap: Adds a circle to the map.
//  12
//  updateMap: Updates the map with a new location and title.
//  13
//  hideKeyboard: Hides the keyboard.
//  14
//  direction: Requests and displays directions on the map.
//  15
//  decodePolyline: Helper method to decode polyline points.