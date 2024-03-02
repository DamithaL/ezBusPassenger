package ezbus.mit20550588.passenger.ui.PurchaseTicket;

import static ezbus.mit20550588.passenger.util.Constants.Log;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ezbus.mit20550588.passenger.data.model.PayHereRequest;
import ezbus.mit20550588.passenger.ui.Login.Login;
import ezbus.mit20550588.passenger.util.Constants;
import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class PayHere {

    private static final int PAYHERE_REQUEST = 11001;

    public void initiatePayHerePayment(Context context, PayHereRequest payHereRequest) {
        // Initialize PayHere
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);

        Log("initiatePayHerePayment", "started");

        // Create an InitRequest object and set the required parameters
        InitRequest req = createInitRequest(payHereRequest);

        // Set the NotifyUrl
      req.setNotifyUrl("http://localhost:3000/payment/initiate");


//        req.setNotifyUrl("http://localhost:3000/payment/payhere/notify");

        Log("initiatePayHerePayment", "req", req.toString());

        // req.setNotifyUrl("https://your-nodejs-server.com/payhere/notify"); // Replace with your actual server's endpoint

        Log("initiatePayHerePayment", "req sent to NotifyUrl");

        startPayHereActivity(context, req);
    }

    private InitRequest createInitRequest(PayHereRequest payHereRequest) {
        InitRequest req = new InitRequest();

        Log("createInitRequest", "payHereRequest", payHereRequest.toString());


        req.setMerchantId(payHereRequest.getMerchantId());
        req.setCurrency(payHereRequest.getCurrency());
        req.setAmount(payHereRequest.getAmount());
        req.setOrderId(payHereRequest.getOrderId());
        req.setItemsDescription(payHereRequest.getItemsDescription());
        req.setCustom1(payHereRequest.getCustom1() != null ? payHereRequest.getCustom1() : "");
        req.setCustom2(payHereRequest.getCustom2() != null ? payHereRequest.getCustom2() : "");
        req.getCustomer().setFirstName(payHereRequest.getFirstName());
        req.getCustomer().setLastName(payHereRequest.getLastName() != null ? payHereRequest.getLastName() : "");
        req.getCustomer().setEmail(payHereRequest.getEmail());
        req.getCustomer().setPhone(payHereRequest.getPhone() != null ? payHereRequest.getPhone() : "");
        req.getCustomer().getAddress().setAddress(payHereRequest.getAddress() != null ? payHereRequest.getAddress() : "");
        req.getCustomer().getAddress().setCity(payHereRequest.getCity() != null ? payHereRequest.getCity() : "");
        req.getCustomer().getAddress().setCountry(payHereRequest.getCountry());
////
        // req.setMerchantId("1225343");
        // req.setCurrency("LKR");
        //  req.setAmount(1000.00);
        //   req.setOrderId("230000123");
        //  req.setItemsDescription("Door bell wireless");
        //  req.setCustom1("This is the custom message 1");
        // req.setCustom2("This is the custom message 2");
        // req.getCustomer().setFirstName("Saman");
      //  req.getCustomer().setLastName("Perera");
        //  req.getCustomer().setEmail("samanp@gmail.com");
        //  req.getCustomer().setPhone("+94771234567");
      //  req.getCustomer().getAddress().setAddress("No.1, Galle Road");
      //  req.getCustomer().getAddress().setCity("Colombo");
        //   req.getCustomer().getAddress().setCountry("Sri Lanka");

        return req;
    }

    public void startPayHereActivity(Context context, InitRequest req) {
        Intent intent = new Intent(context, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).startActivityForResult(intent, PAYHERE_REQUEST);
        }
    }

    public void handleActivityResult(
            AppCompatActivity activity, int requestCode, int resultCode, Intent data) {
        Log("onActivityResult", "started");

        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response =
                    (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

            Log("onActivityResult", "resultCode", String.valueOf(resultCode));
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Log("onActivityResult", "RESULT_OK");
                handlePaymentSuccess(activity, response);
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                Log("onActivityResult", "RESULT_CANCELED");
                handlePaymentCancel(activity, response);
            }
        }
    }

    private void handlePaymentSuccess(
            Context context, PHResponse<StatusResponse> response) {
        String message;
        if (response != null && response.isSuccess()) {
            message = "Payment successful. Details: " + response.getData().toString();
        } else {
            message = "Payment failed. Details: " + response.toString();
        }
        Log("handlePaymentSuccess", message);
        Log("handlePaymentSuccess", "message", message);

     //  Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private void handlePaymentCancel(
            Context context, PHResponse<StatusResponse> response) {
        String message;
        if (response != null) {
            message = "Payment canceled. Details: " + response.toString();
        } else {
            message = "User canceled the payment request";
        }

        Log("handlePaymentCancel", "message", message);
        //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
