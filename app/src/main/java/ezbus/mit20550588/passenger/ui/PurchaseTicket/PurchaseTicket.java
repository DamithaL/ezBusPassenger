package ezbus.mit20550588.passenger.ui.PurchaseTicket;

import static ezbus.mit20550588.passenger.util.Constants.Log;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.Map;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.data.model.PurchasedTicketModel;
import ezbus.mit20550588.passenger.data.model.TicketModel;
import ezbus.mit20550588.passenger.data.model.TicketOrder;
import ezbus.mit20550588.passenger.data.viewModel.PaymentViewModel;
import ezbus.mit20550588.passenger.data.viewModel.PurchasedTicketViewModel;
import ezbus.mit20550588.passenger.util.DateUtils;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.StatusResponse;

public class PurchaseTicket extends AppCompatActivity {

    private Button payButton;
    private TicketModel ticket;
    private PayHere payHereInstance;

    private String orderID;
    private PaymentViewModel paymentViewModel;
    private static final int PAYHERE_REQUEST = 11001;

    private PurchasedTicketViewModel purchasedTicketViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_ticket);

        // Retrieve the TicketModel object from the Intent
        Intent intent = getIntent();
        ticket = (TicketModel) intent.getSerializableExtra("ticket");

        // Display route information and ticket details
        if (ticket != null) {
            displayTicketDetails(ticket);
        }

        initializePaymentViewModel();

        // Initialize the class-level variable
        payHereInstance = new PayHere();

        initializeButtons();

    }

    private void initializePaymentViewModel() {
        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

        // Observe ticket order live data -- this is for sending ticket data to PayHere Instance
        paymentViewModel.getTicketOrderLiveData().observe(this, payHereRequest -> {
            if (payHereRequest != null) {
                Log("getTicketOrderLiveData", "payHereRequest", payHereRequest.toString());

                // Ensure payHereInstance is not null before calling initiatePayHerePayment
                if (payHereInstance != null) {

                    if (payHereRequest.getOrderId() != null) {
                        orderID = payHereRequest.getOrderId();
                        // Call the initiatePayHerePayment method
                        payHereInstance.initiatePayHerePayment(this, payHereRequest);
                    }

                }
            }
        });

        // Observe payment validation status -- this is for sending payment data to server for verification and storing
        paymentViewModel.getPaymentStatusLiveData().observe(this, paymentStatus -> {
            if (paymentStatus != null) {
                Log("getPaymentStatusLiveData", "paymentStatus", paymentStatus.toString());

                if (paymentStatus.getStatus() == 200) {
                    Log("getPaymentStatusLiveData", paymentStatus.getMessage());

                    // PAYMENT IS SUCCESSFUL AND VERIFIED/STORED BY THE SERVER

                    // NOW STORE IN LOCAL DATABASE
                    // Initialize the ViewModel
                    purchasedTicketViewModel = new ViewModelProvider(this).get(PurchasedTicketViewModel.class);

                    PurchasedTicketModel newTicket = new PurchasedTicketModel(
                            ticket,
                            orderID,
                            DateUtils.parseDate(DateUtils.getCurrentDate()),
                            false,
                            null);

                    purchasedTicketViewModel.insert(newTicket);

                    Intent intent = new Intent(PurchaseTicket.this, MyTickets.class);
                    startActivity(intent);

                } else {
                    Log("getPaymentStatusLiveData", "ERROR", paymentStatus.getMessage());
                    Toast.makeText(PurchaseTicket.this, paymentStatus.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Observe the error message LiveData
        paymentViewModel.getErrorLiveData().observe(this, errorMessage -> {
            if (errorMessage != null) {
                // Update your UI to display the error message (e.g., show a Toast or update a TextView)
                Log("getErrorLiveData", errorMessage);
                TextView errorTextView = findViewById(R.id.errorMessageTextView);

                errorTextView.setText(errorMessage);
                //  showToast(ErrorResponse);
            }
        });
    }

    private void initializeButtons() {
        // Back button
        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the current activity
                finish();
            }
        });

        // Yes button
        Button yesButton = findViewById(R.id.YesButton);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log("Yes button", "clicked");

                // Send the ticket order to server to validate
                TicketOrder newTicketOrder = new TicketOrder(ticket);
                // Trigger validate in ViewModel
                paymentViewModel.validateTicketOrder(newTicketOrder);

                Log("Yes button clicked", "ticket order", newTicketOrder.toString());

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

    private void displayTicketDetails(TicketModel ticket) {
        TextView routeNumberTextView = findViewById(R.id.routeTextView);
        TextView startStopTextView = findViewById(R.id.startStopTextView);
        TextView endStopTextView = findViewById(R.id.endStopTextView);
        TextView farePriceTextView = findViewById(R.id.farePriceTextView);

        routeNumberTextView.setText(ticket.getRouteNumber() + " | " + ticket.getRouteName());
        startStopTextView.setText(ticket.getArrivalStopName());
        endStopTextView.setText(ticket.getDepartureStopName());
        farePriceTextView.setText("Rs." + String.format("%.2f", ticket.getFarePrice()));
    }

    // This is results from PayHere instance -- This will be verified next from the server by getPaymentStatusLiveData observation
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log("onActivityResult", "started");

        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

            Log("onActivityResult", "resultCode", String.valueOf(resultCode));

            if (resultCode == Activity.RESULT_OK) {

                Log("onActivityResult", "RESULT_OK");
                handlePaymentDone(response);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log("onActivityResult", "RESULT_CANCELED");
                handlePaymentCancel(response);
            }
        }
    }

    private void handlePaymentDone(
            PHResponse<StatusResponse> response) {

        if (response != null && response.isSuccess()) {

            Log("Payment successful. Details", response.getData().toString());

            Map<String, Object> paymentStatus = new HashMap<>();
            paymentStatus.put("orderId", orderID);
            paymentStatus.put("paymentNo", response.getData().getPaymentNo());
            paymentStatus.put("purchasedDate", DateUtils.getCurrentDate());
            paymentViewModel.notifyPayment(paymentStatus);

        } else {
            // this happens when user enters a wrong card details or bank denies the payment
            Log("Payment failed. Details", "Result: no response");
            Map<String, Object> paymentStatus = new HashMap<>();
            paymentStatus.put("orderId", orderID);
            paymentStatus.put("isOrderCancelled", true);
            paymentViewModel.notifyPaymentCancel(paymentStatus);
        }
    }

    private void handlePaymentCancel(
            PHResponse<StatusResponse> response) {

        if (response != null) {
            Log("Payment canceled. Details", "message", response.toString());
        } else {
            Log("Payment canceled. Details", "User canceled the payment request");
        }

        Map<String, Object> paymentStatus = new HashMap<>();
        paymentStatus.put("orderId", orderID);
        paymentStatus.put("isOrderCancelled", true);
        paymentViewModel.notifyPaymentCancel(paymentStatus);


    }

}