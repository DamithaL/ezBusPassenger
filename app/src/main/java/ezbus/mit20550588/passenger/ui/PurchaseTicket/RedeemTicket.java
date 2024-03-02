package ezbus.mit20550588.passenger.ui.PurchaseTicket;

import org.json.JSONException;
import org.json.JSONObject;

import static ezbus.mit20550588.passenger.util.Constants.Log;
import static ezbus.mit20550588.passenger.util.Converters.timestampToFormattedTime;
import static ezbus.mit20550588.passenger.util.PasswordHash.hashPasswordSHA;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.data.model.PurchasedTicketModel;
import ezbus.mit20550588.passenger.data.model.TicketModel;
import ezbus.mit20550588.passenger.data.viewModel.PaymentViewModel;
import ezbus.mit20550588.passenger.data.viewModel.PurchasedTicketViewModel;
import ezbus.mit20550588.passenger.ui.MainActivity;
import ezbus.mit20550588.passenger.util.DateUtils;
import ezbus.mit20550588.passenger.util.QRCodeGenerator;
import ezbus.mit20550588.passenger.util.UserStateManager;

public class RedeemTicket extends AppCompatActivity {

    private PurchasedTicketModel ticket;
    private PaymentViewModel paymentViewModel;
    private PurchasedTicketViewModel purchasedTicketViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_ticket);

        Intent intent = getIntent();
        ticket = (PurchasedTicketModel) intent.getSerializableExtra("ticket");

        // PurchasedTicketModel receivedTicket = (PurchasedTicketModel) getIntent().getSerializableExtra("ticket");

        // Back button
        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the current activity
                finish();
            }
        });


        TextView busDetails_route = findViewById(R.id.busDetails_Route);
        TextView busDetails_GetOnPlace = findViewById(R.id.busDetails_GetOnPlace);
        TextView busDetails_GetDownPlace = findViewById(R.id.busDetails_GetDownPlace);
        TextView busDetails_TicketPrice = findViewById(R.id.busDetails_TicketPrice);
        ImageView qrImage = findViewById(R.id.QRImageView);
        ImageView errorImage = findViewById(R.id.errorImageView);
        TextView errorMessageTextView = findViewById(R.id.errorMessageTextView);
        findViewById(R.id.errorView).setVisibility(View.GONE);


        if (ticket != null) {
            String routeText = ticket.getTicket().getRouteNumber() + " | " + ticket.getTicket().getRouteName();
            busDetails_route.setText(routeText);
            busDetails_GetOnPlace.setText(ticket.getTicket().getArrivalStopName());
            busDetails_GetDownPlace.setText(ticket.getTicket().getDepartureStopName());
            String price = "Rs. " + String.valueOf(ticket.getTicket().getFarePrice());
            busDetails_TicketPrice.setText(price);

            // Show QR Code

            // TicketOrderId
            // UserEmail
            // Not now  - Date time --- so after a some period of time we can just reject the ticket redeem request if we want.
            //             ---- if it implements, there should be also another way to share tickets (ex: for elderly or children)

            // Create a JSON object
            JSONObject jsonObject = new JSONObject();

            UserStateManager userManager = UserStateManager.getInstance();
            String userEmail = userManager.getUser().getEmail();
            String hashedEmail = hashPasswordSHA(userEmail);
            String orderId = ticket.getOrderId();
            try {
                jsonObject.put("hashedEmail", hashedEmail);
                jsonObject.put("orderId", orderId);
            } catch (JSONException e) {
                Log("RedeemTicket", "QRCodeGen: ERROR", e.getMessage());
            }
            // Convert the JSON object to a string
            String qrCodeDatajsonString = jsonObject.toString();



            Log("RedeemTicket", "QRCode", qrCodeDatajsonString);

            // CHECK IF TICKET IS ALREADY REDEEMED
            paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

            Map<String, String> checkRedeemStatusRequest = new HashMap<>();
            checkRedeemStatusRequest.put("orderId", orderId);
            checkRedeemStatusRequest.put("hashedEmail", hashedEmail);
            paymentViewModel.isTicketRedeemed(checkRedeemStatusRequest);

            // Observe ticket order live data -- this is for sending ticket data to PayHere Instance
            paymentViewModel.getTicketRedemptionStatus().observe(this, ticketRedemptionStatus -> {
                if (ticketRedemptionStatus != null) {
                    Log("RedeemTicket", "ticketRedemptionStatus",ticketRedemptionStatus.toString());

                    if (ticketRedemptionStatus.isValid()){
                        Log("RedeemTicket", "ticketRedemptionStatus", "VALID");
                        Bitmap qrBitMap = QRCodeGenerator.generateQRCode(qrCodeDatajsonString, 400, 400);
                        qrImage.setImageBitmap(qrBitMap);

                    } else {
                        Log("RedeemTicket", "ticketRedemptionStatus", "ALREADY REDEEMED");
                        errorImage.setImageResource(R.drawable.ic_successful);
                        errorMessageTextView.setText("Ticket has already been redeemed. Updating status now");
                        errorMessageTextView.setTextColor(Color.argb(255,0, 153, 31));
                        findViewById(R.id.errorView).setVisibility(View.VISIBLE);

                        Log("RedeemTicket", "ticketRedemptionStatus", ticketRedemptionStatus.toString());

                        ticket.setRedeemed(true);
                        purchasedTicketViewModel = new ViewModelProvider(this).get(PurchasedTicketViewModel.class);
                        purchasedTicketViewModel.update(ticket, ticketRedemptionStatus.getRedeemedDate());

                        Toast.makeText(RedeemTicket.this, "Updated ticket status", Toast.LENGTH_SHORT).show();
                        Log("RedeemTicket", "ticketRedemptionStatus", "Updated");


                    }
                }
            });

            // Observe the error message LiveData
            paymentViewModel.getErrorLiveData().observe(this, errorMessage -> {
                if (errorMessage != null) {
                    Log("RedeemTicket","getErrorLiveData", errorMessage);
                    errorImage.setImageResource(R.drawable.ic_error);
                    errorMessageTextView.setText(errorMessage);
                    findViewById(R.id.errorView).setVisibility(View.VISIBLE);

                }
            });

        } else {
            Log("Ticket", "is null");
            qrImage.setImageResource(R.drawable.ic_error);
            qrImage.setPadding(20,20,20,20);
            errorMessageTextView.setText("No valid ticket found");
            errorMessageTextView.setVisibility(View.VISIBLE);

        }
    }
}