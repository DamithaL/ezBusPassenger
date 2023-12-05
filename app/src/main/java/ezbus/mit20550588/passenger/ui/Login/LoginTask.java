package ezbus.mit20550588.passenger.ui.Login;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL("http://localhost:3000/login");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                // Set up the request
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                // Build the JSON object with user credentials
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("username", params[0]);
                jsonParams.put("password", params[1]);

                // Send the POST request
                OutputStream os = urlConnection.getOutputStream();
                os.write(jsonParams.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException | JSONException e) {
            Log.e("LoginTask", "Error", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                JSONObject jsonResponse = new JSONObject(result);

                // Process the JSON response
                boolean success = jsonResponse.getBoolean("success");
                String message = jsonResponse.getString("message");

                if (success) {
                    // Login successful
                    Log.d("LoginTask", message);

                    // Extract and save the token if needed
                    String token = jsonResponse.getString("token");
                } else {
                    // Login failed
                    Log.e("LoginTask", message);
                }
            } catch (JSONException e) {
                Log.e("LoginTask", "Error parsing JSON", e);
            }
        } else {
            // Handle error
            Log.e("LoginTask", "Failed to perform login");
        }
    }
}

