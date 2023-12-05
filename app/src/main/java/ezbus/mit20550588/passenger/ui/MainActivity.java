package ezbus.mit20550588.passenger.ui;


import static androidx.constraintlayout.widget.Constraints.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
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
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import ezbus.mit20550588.passenger.adapters.PlacesAutoCompleteAdapter;
import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.ui.Settings.Settings;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PlacesAutoCompleteAdapter.ClickListener {

    // widgets
    private GoogleMap myMap;
    private SearchView mapSearchView;
    private ProgressBar loadingProgressBar;
    private ImageView mGps;

    // constants
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
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private RecyclerView recyclerView;
    private LatLng SourceLocationLatLng;
    private LatLng DestinationLocationLatLng;
    private Polyline directionRoutePolyline;
//    private ArrayList<BusLocation>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        isServicesOK();
        getLocationPermission();


        // PLACES
        Places.initialize(this, getResources().getString(R.string.MAPS_API_KEY));

        recyclerView = (RecyclerView) findViewById(R.id.places_recycler_view);
        ((EditText) findViewById(R.id.place_search)).addTextChangedListener(filterTextWatcher);

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAutoCompleteAdapter.setClickListener(this);
        recyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();


        // Source Location Text
        EditText SourceLocationText = findViewById(R.id.sourceLocation);

        // Set up an OnClickListener for the SourceLocationText
        SourceLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show or make the RecyclerView visible
                recyclerView.setVisibility(View.VISIBLE);
                RelativeLayout SearchBarLayout = findViewById(R.id.SearchBarRelLayout);
                SearchBarLayout.setVisibility(View.VISIBLE);
            }
        });

        // Initialize Current Location Button

        ImageView mGps = findViewById(R.id.ic_gps);
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });


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

        // Back button
        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show Direction Bar
                RelativeLayout DirectionBar = findViewById(R.id.directionBar);
                DirectionBar.setVisibility(View.GONE);

                // Hide Search bar
                RelativeLayout SearchBarLayout = findViewById(R.id.SearchBarRelLayout);
                SearchBarLayout.setVisibility(View.VISIBLE);

                // Clear Polylines
                clearDirectionRoute();
            }
        });


        // Swap location button
        ImageButton swapButton = findViewById(R.id.SwapButton);
        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                swapLocations();
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


            // Initiate Search Bar
//            initSearchBar();

            // Hide waiting progress bar
            hideProgressBar();

        }

    }


    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (recyclerView.getVisibility() == View.GONE) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };


    public void click(Place place) {

        Toast.makeText(this, place.getAddress() + ", " + place.getLatLng().latitude + place.getLatLng().longitude, Toast.LENGTH_SHORT).show();
        recyclerView.setVisibility(View.GONE);
        moveCamera(place.getLatLng(), DEFAULT_ZOOM, place.getName());
        EditText PlaceSearchEditText = findViewById(R.id.place_search);
        PlaceSearchEditText.setText("");

        DestinationLocationLatLng = place.getLatLng();


        if (SourceLocationLatLng != null) {
            direction("Your current location", SourceLocationLatLng, place.getName().toString(), DestinationLocationLatLng);

            // Show Direction Bar
            RelativeLayout DirectionBar = findViewById(R.id.directionBar);
            DirectionBar.setVisibility(View.VISIBLE);

            // Hide Search bar
            RelativeLayout SearchBarLayout = findViewById(R.id.SearchBarRelLayout);
            SearchBarLayout.setVisibility(View.GONE);

            // Hide keyboard
            // Assume you have a view (e.g., an EditText) where the keyboard is currently open
            View view = findViewById(R.id.place_search);
            hideKeyboard(view);
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
                                SourceLocationLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
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

//    private void initSearchBar() {
//
//        Log.d(TAG, "Search bar initiated");
//
//        mapSearchView = findViewById(R.id.place_search);
//
//
//        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                if (!TextUtils.isEmpty(s) && myMap != null && !isSearchInProgress) {
//
//
//                    isSearchInProgress = true; // Set the flag to indicate search is in progress
//
//                    Log.d(TAG, "Search query submitted");
//                    Log.d(TAG, "Search query: " + s);
//                    try {
//                        searchByLocationName();
//
//                        // Reset the flag after a short delay to prevent rapid consecutive searches
//                        handler.postDelayed(() -> isSearchInProgress = false, 1000);
//
//                    } catch (Exception e) {
//                        Log.e(TAG, "Error during calling searchByLocationName() in setQueryTextListener " + e.getMessage());
//
//
//                    }
////                    // Hide the keyboard when the button is clicked
////                    hideKeyboard();
//                }
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                return false;
//            }
//        });
//    }

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

    private void hideKeyboard(View view) {

        if (view != null) {


            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Add a method to request and display directions
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

    // Method to clear or hide the direction route
    private void clearDirectionRoute() {
        if (directionRoutePolyline != null) {
            directionRoutePolyline.remove();
            directionRoutePolyline = null;
        }
    }

    private void swapLocations() {

        clearDirectionRoute();

        EditText SourceLocation = findViewById(R.id.sourceLocation);
        EditText DestinationLocation = findViewById(R.id.destinationLocation);
        Editable temp;
        LatLng tempLatLng;

        Log.d(TAG, "Before SourceLocationLatLng: "+ SourceLocationLatLng);
        Log.d(TAG, "Before DestinationLocationLatLng: "+  DestinationLocationLatLng);



        if (SourceLocation.getText() != null && DestinationLocation.getText() != null) {
            temp = SourceLocation.getText();
            SourceLocation.setText(DestinationLocation.getText());
            DestinationLocation.setText(temp);

            tempLatLng = SourceLocationLatLng;
            SourceLocationLatLng = DestinationLocationLatLng;
            DestinationLocationLatLng = tempLatLng;

            Log.d(TAG, "SourceLocation.getText().toString(): "+ SourceLocation.getText().toString());
            Log.d(TAG, "SourceLocationLatLng: "+ SourceLocationLatLng);
            Log.d(TAG, "DestinationLocation.getText().toString(): "+ DestinationLocation.getText().toString());
            Log.d(TAG, "DestinationLocationLatLng: "+  DestinationLocationLatLng);


            direction(SourceLocation.getText().toString(), SourceLocationLatLng, DestinationLocation.getText().toString(), DestinationLocationLatLng);




        }
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