package ezbus.mit20550588.passenger.data.repository;

import static ezbus.mit20550588.passenger.util.Constants.Log;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.Map;

import ezbus.mit20550588.passenger.data.model.PayHereRequest;
import ezbus.mit20550588.passenger.data.model.TicketOrder;
import ezbus.mit20550588.passenger.data.network.ApiServiceBus;
import ezbus.mit20550588.passenger.data.network.ApiServicePayment;
import ezbus.mit20550588.passenger.data.network.PaymentStatusResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentRepository {

    private ApiServicePayment apiService;
    private MutableLiveData<PayHereRequest> ticketOrderLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private MutableLiveData<PaymentStatusResponse> paymentStatusLiveData = new MutableLiveData<>();

    public PaymentRepository(ApiServicePayment apiService) {
        this.apiService = apiService;
    }

    public MutableLiveData<PayHereRequest> getTicketOrderLiveData() {
        return ticketOrderLiveData;
    }

    public MutableLiveData<PaymentStatusResponse> getPaymentStatusLiveData() {
        return paymentStatusLiveData;
    }

    public void validateTicketOrder(TicketOrder ticketOrder) {
        Call<PayHereRequest> call = apiService.validateTicketOrder(ticketOrder);

        call.enqueue(new Callback<PayHereRequest>() {
            @Override
            public void onResponse(Call<PayHereRequest> call, Response<PayHereRequest> response) {
                if (response.isSuccessful()) {

                    PayHereRequest payHereRequest = response.body();

                    try {
                        if (payHereRequest != null) {
                            // Notify the callback
                            errorLiveData.setValue(null);
                            ticketOrderLiveData.setValue(payHereRequest);


                        }
                    } catch (NumberFormatException e) {
                        // Handle the case where the string is not a valid double
                        //   handleInvalidDoubleFormat();
                    }


                } else {
                    String errorMessage = "Failed to validate the ticket order. ";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    errorLiveData.setValue(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<PayHereRequest> call, Throwable t) {
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }


    public void notifyPayment(Map<String, Object> notifyPaymentRequest) {
        Call<Void> call = apiService.notifyPayment(notifyPaymentRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                try {

                    Log("notifyPayment","onResponse", response.toString());
                    PaymentStatusResponse paymentStatusResponse = new PaymentStatusResponse();
                    paymentStatusResponse.setStatus(response.code());
                    Log("notifyPayment","onResponse: Code", String.valueOf(response.code()));
                    paymentStatusResponse.setMessage("Payment status updated successfully");
                    Log("notifyPayment","onResponse: body", "Payment status updated successfully");

                    if (response.isSuccessful()) {

                        // Notify the callback
                        errorLiveData.setValue(null);
                        paymentStatusLiveData.setValue(paymentStatusResponse);

                        Log("notifyPayment","onResponse","SUCCESSFUL");

                    } else {
                        String errorMessage = "Failed to validate the ticket order. ";
                        if (response.errorBody() != null) {
                            try {
                                errorMessage += response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        errorLiveData.setValue(errorMessage);
                        Log("notifyPayment","ERROR",errorMessage);
                    }
                } catch (Exception e) {
                    errorLiveData.setValue(e.getMessage());
                    Log("notifyPayment","ERROR e", e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorLiveData.setValue("Network error: " + t.getMessage());
                Log("notifyPayment","onFailure: ERROR e", t.getMessage());
            }
        });
    }
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void notifyPaymentCancel(Map<String, Object> notifyPaymentRequest) {
        Call<Void> call = apiService.notifyPaymentCancel(notifyPaymentRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log("notifyPaymentCancel","onResponse", response.toString());
                } else {
                    Log("notifyPaymentCancel","onResponse: ERROR", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log("notifyPaymentCancel","onFailure", t.getMessage());
            }
        });
    }

}
