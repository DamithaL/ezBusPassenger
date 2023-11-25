package ezbus.mit20550588.passenger;


import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private EditText longitudeText;
    private EditText latitudeText;
    private EditText placeText;
    private Switch switchButton;
    private TableRow bannerTableRow;
    private SearchView mapSearchView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

//        TableRow bannerTableRow = (TableRow) findViewById(R.id.bannerTableRow);
//        bannerTableRow.setVisibility(View.GONE);


        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_main);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mapSearchView = findViewById(R.id.mapSearch);

//        mapSearchView.setVisibility(View.GONE);


        bannerTableRow = findViewById(R.id.bannerTableRow);
        bannerTableRow.setVisibility(View.GONE);

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (myMap != null) {
                    searchByLocationName();
                }

                // Hide the keyboard when the button is clicked
                hideKeyboard();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

//        View.OnClickListener listener = new View.OnClickListener() {
//            public void onClick(View view) {
//                virtualTicket();
//            }
//        };
//        findViewById(R.id.button).setOnClickListener(listener);

//        Button settings = (Button) findViewById(R.id.fab);
//        settings.setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View view) { settings(); } });

        // Initialize fab
        fab = findViewById(R.id.fab);

        // Set an OnClickListener for the fab
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the SettingsActivity when the fab is clicked
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });

        Button purchase = (Button) findViewById(R.id.button);
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { virtualTicket(); } });


    }

    private void virtualTicket() {
        Intent intent = new Intent(this, virtualTicket.class); startActivity(intent); }

    private void settings() {
        Intent intent = new Intent(this, Settings.class); startActivity(intent); }

    public boolean searchByLocationName() {
        String locationInput = mapSearchView.getQuery().toString();
        List<Address> addressList = null;

        if (locationInput != null) {
            Geocoder geocoder = new Geocoder(MainActivity.this);

            if (TextUtils.isEmpty(locationInput)) {
                Toast.makeText(MainActivity.this, "Please enter a location name.", Toast.LENGTH_SHORT).show();
            }

            else {
                try {
                    addressList = geocoder.getFromLocationName(locationInput, 1);
                    Address address = addressList.get(0);
                    LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
                    updateMap(latlng, locationInput);

                    LatLng ucsc = new LatLng(6.90246, 79.86115);
                    LatLng bus = new LatLng(6.896667, 79.859973);
                    addCircleToMap(bus, 50); // 500 meters radius

                    // Set an OnCircleClickListener for the circle
                    myMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                        @Override
                        public void onCircleClick(Circle clickedCircle) {
                            // Handle the circle click here
                            if (clickedCircle.getTag() != null) {
                                String circleTag = clickedCircle.getTag().toString();
                                // Do something based on the circle's tag (if needed)
                                if (circleTag.equals("Your circle's tag")) {
                                    // Perform the desired action when the circle is clicked
                                    // Example: Show a toast message
//                                    Toast.makeText(MainActivity.this, "Circle clicked!", Toast.LENGTH_SHORT).show();
                                    bannerTableRow.setVisibility(View.VISIBLE); // Show the banner TableRow

                                }
                            }
                        }
                    });

                    direction(ucsc,latlng);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Place not found.", Toast.LENGTH_SHORT).show();
                }
            }
        }

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

    private void updateMap(LatLng location, String title) {
        if (myMap != null) {

            // Set the maximum zoom level
            float maxZoom = 16.0f;
            myMap.setMaxZoomPreference(maxZoom);

            // Remove any existing markers
            myMap.clear();

            // Add marker to the location
            myMap.addMarker(new MarkerOptions().position(location).title(title));

            // Move camera to the location and set the desired zoom level
            float desiredZoomLevel = 16.0f; //
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, desiredZoomLevel));

            // Enable zoom controls
            myMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        myMap = googleMap;


        // Set the maximum zoom level
        float maxZoom = 16.0f;
        myMap.setMaxZoomPreference(maxZoom);

        LatLng ucsc = new LatLng(6.90246, 79.86115);
        LatLng ucsc2 = new LatLng(6.9271, 79.8612);

        // Remove any existing markers
        myMap.clear();

        // Add marker to the location
        myMap.addMarker(new MarkerOptions().position(ucsc).title("UCSC"));

        // Move camera to the location and set the desired zoom level
        float desiredZoomLevel = 16.0f; // Change this value as per your requirement
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ucsc, desiredZoomLevel));

        // Enable zoom controls
        myMap.getUiSettings().setZoomControlsEnabled(true);


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

}