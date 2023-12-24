package ezbus.mit20550588.passenger.ui.PurchaseTicket;

import static ezbus.mit20550588.passenger.util.Constants.BASE_URL;
import static ezbus.mit20550588.passenger.util.Constants.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.stripe.android.paymentsheet.addresselement.AddressDetails;
import com.stripe.android.paymentsheet.addresselement.AddressLauncher;
import com.stripe.android.paymentsheet.addresselement.AddressLauncherResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.data.model.TicketModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PurchaseTicket extends AppCompatActivity {

    private String paymentIntentClientSecret;
    private PaymentSheet paymentSheet;


    private Button payButton;
    private AddressLauncher addressLauncher;

    private AddressDetails shippingDetails;

    private Button addressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_ticket);

        // Retrieve the TicketModel object from the Intent
        Intent intent = getIntent();
        TicketModel ticket = (TicketModel) intent.getSerializableExtra("ticket");

        // Display route information and ticket details
        if (ticket != null) {
            displayTicketDetails(ticket);
        }

        // Back button
        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the current activity
                finish();
            }
        });

        // Hook up the pay button
        payButton = findViewById(R.id.YesButton);
        payButton.setOnClickListener(this::onPayClicked);
        payButton.setEnabled(false);

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        // Hook up the address button
       // addressButton = findViewById(R.id.address_button);
        // addressButton.setOnClickListener(this::onAddressClicked);
      //  addressLauncher = new AddressLauncher(this, this::onAddressLauncherResult);

        fetchPaymentIntent();

    }

    private void showAlert(String title, @Nullable String message) {
        runOnUiThread(() -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Ok", null)
                    .create();
            dialog.show();
        });
    }


    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    private void fetchPaymentIntent() {
        final String shoppingCartContent = "{\"items\": [ {\"id\":\"xl-tshirt\"}]}";

        final RequestBody requestBody = RequestBody.create(
                MediaType.get("application/json; charset=utf-8"),
                shoppingCartContent
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "payment/create-payment-intent")
                .post(requestBody)
                .build();

        new OkHttpClient()
                .newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        showAlert("Failed to load data", "Error: " + e.toString());
                    }

                    @Override
                    public void onResponse(
                            @NonNull Call call,
                            @NonNull Response response
                    ) throws IOException {
                        if (!response.isSuccessful()) {
                            showAlert(
                                    "Failed to load page",
                                    "Error: " + response.toString()
                            );
                        } else {
                            final JSONObject responseJson = parseResponse(response.body());
                            paymentIntentClientSecret = responseJson.optString("clientSecret");
                            runOnUiThread(() -> payButton.setEnabled(true));
                            Log( "fetchPaymentIntent","Retrieved PaymentIntent");
                        }
                    }
                });
    }


    private void onPaymentSheetResult(
            final PaymentSheetResult paymentSheetResult
    ) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            showToast("Payment complete!");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log( "onPaymentSheetResult","Payment canceled!");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Throwable error = ((PaymentSheetResult.Failed) paymentSheetResult).getError();
            showAlert("Payment failed", error.getLocalizedMessage());
        }
    }

    private void onAddressLauncherResult(AddressLauncherResult result) {
        // TODO: Handle result and update your UI
        if (result instanceof AddressLauncherResult.Succeeded) {
            shippingDetails = ((AddressLauncherResult.Succeeded) result).getAddress();
        } else if (result instanceof AddressLauncherResult.Canceled) {
            // TODO: Handle cancel
        }
    }

    private JSONObject parseResponse(ResponseBody responseBody) {
        if (responseBody != null) {
            try {
                return new JSONObject(responseBody.string());
            } catch (IOException | JSONException e) {
                Log( "parseResponse","Error parsing response", e.getMessage());

            }
        }

        return new JSONObject();
    }

    private void onPayClicked(View view) {
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration("Example, Inc.");

        // Present Payment Sheet
        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
    }


    private void displayTicketDetails(TicketModel ticket) {
        TextView routeNumberTextView = findViewById(R.id.routeTextView);
        TextView startStopTextView = findViewById(R.id.startStopTextView);
        TextView endStopTextView = findViewById(R.id.endStopTextView);
        TextView farePriceTextView = findViewById(R.id.farePriceTextView);

        routeNumberTextView.setText(ticket.getRouteNumber()+ " | "+ ticket.getRouteName());
        startStopTextView.setText(ticket.getArrivalStopName());
        endStopTextView.setText(ticket.getDepartureStopName());
        farePriceTextView.setText("Rs." + String.format("%.2f", ticket.getFarePrice()));

        // Yes button
        Button yesButton = findViewById(R.id.YesButton);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: 2023-12-23

            }
        });

        // No button
        Button noButton = findViewById(R.id.NoButton);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the current activity
                finish();
            }
        });
    }


}