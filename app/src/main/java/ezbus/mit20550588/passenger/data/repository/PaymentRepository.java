package ezbus.mit20550588.passenger.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import ezbus.mit20550588.passenger.data.model.BusModel;
import ezbus.mit20550588.passenger.data.remote.ApiServiceBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentRepository {
    private ApiServiceBus busApiService;
    private MutableLiveData<Double> fareLiveData = new MutableLiveData<>();
        private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public PaymentRepository(ApiServiceBus busApiService) {
        this.busApiService = busApiService;
    }

    public LiveData<Double> getFarePriceLiveData() {
        return fareLiveData;
    }


    public void getFarePrice(String routeId, String startBusStop, String endBusStop) {
        Call<String> call = busApiService.getFare(routeId, startBusStop, endBusStop);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {

                    String responseBody = response.body();

                    try {
                        if (responseBody != null) {
                            // Convert the string to a double
                            double farePrice = Double.parseDouble(responseBody);

                            // Notify the callback with the fare price
                            fareLiveData.setValue(farePrice);

                        }
                    } catch (NumberFormatException e) {
                        // Handle the case where the string is not a valid double
                     //   handleInvalidDoubleFormat();
                    }



                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }


    private void handleErrorResponse(Response<?> response) {
        String errorMessage = "Failed to calculate the ticket price. ";
        if (response.errorBody() != null) {
            try {
                errorMessage += response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        errorLiveData.setValue(errorMessage);
    }


    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

}
