package ezbus.mit20550588.passenger.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import ezbus.mit20550588.passenger.data.model.BusLocationModel;
import ezbus.mit20550588.passenger.data.model.BusModel;
import ezbus.mit20550588.passenger.data.model.UserModel;
import ezbus.mit20550588.passenger.data.remote.ApiServiceAuthentication;
import ezbus.mit20550588.passenger.data.remote.ApiServiceBus;
import ezbus.mit20550588.passenger.data.remote.LoginRequest;
import ezbus.mit20550588.passenger.data.remote.RegistrationRequest;
import ezbus.mit20550588.passenger.data.viewModel.AuthResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusLocationRepository {

    private MutableLiveData<List<BusModel>> busLocationsLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private ApiServiceBus busApiService;

    public BusLocationRepository(ApiServiceBus busApiService) {
        this.busApiService = busApiService;
    }

    public LiveData<List<BusModel>> getBusLocationsLiveData() {
        return busLocationsLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void getBusLocations(String routeId) {
        Call<List<BusModel>> call = busApiService.getBuses(routeId);

        call.enqueue(new Callback<List<BusModel>>() {
            @Override
            public void onResponse(Call<List<BusModel>> call, Response<List<BusModel>> response) {
                if (response.isSuccessful()) {
                    List<BusModel> busses = response.body();
                    //List<BusLocationModel> busLocations = response.body();
                    busLocationsLiveData.setValue(busses);
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<List<BusModel>> call, Throwable t) {
                errorLiveData.setValue("Network error: " + t.getMessage());
            }
        });
    }

    private void handleErrorResponse(Response<?> response) {
        String errorMessage = "Failed to fetch bus locations. ";
        if (response.errorBody() != null) {
            try {
                errorMessage += response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        errorLiveData.setValue(errorMessage);
    }


    public void resetData() {
        // Clear the data when necessary
        busLocationsLiveData.setValue(Collections.emptyList());
    }



}
