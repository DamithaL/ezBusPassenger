package ezbus.mit20550588.passenger.data.repository;

import static ezbus.mit20550588.passenger.util.Constants.LOCATION_UPDATE_INTERVAL;
import static ezbus.mit20550588.passenger.util.Constants.Log;

import android.os.Handler;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import ezbus.mit20550588.passenger.data.model.BusLocationModel;
import ezbus.mit20550588.passenger.data.network.ApiServiceBus;
import ezbus.mit20550588.passenger.data.network.ErrorResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusLocationRepository {

    private MutableLiveData<List<BusLocationModel>> busLocationsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<BusLocationModel>> updatedBusLocationsLiveData = new MutableLiveData<>();

    private MutableLiveData<List<BusLocationModel>> hasPassedBusStopLiveData = new MutableLiveData<>();
    private MutableLiveData<ErrorResponse> errorLiveData = new MutableLiveData<>();

    private ApiServiceBus busApiService;

    private Handler handler = new Handler();
    private Runnable updateBusLocationsRunnable;

    public BusLocationRepository(ApiServiceBus busApiService) {
        this.busApiService = busApiService;
    }

    public LiveData<List<BusLocationModel>> getBusLocationsLiveData() {
        return busLocationsLiveData;
    }

    public LiveData<List<BusLocationModel>> getUpdatedBusLocationsLiveData() {
        return updatedBusLocationsLiveData;
    }

    public LiveData<ErrorResponse> getErrorLiveData() {
        return errorLiveData;
    }

    public void getBusLocations(String routeNumber, String routeName) {
        Call<List<BusLocationModel>> call = busApiService.getBuses(routeNumber, routeName);

        call.enqueue(new Callback<List<BusLocationModel>>() {
            @Override
            public void onResponse(Call<List<BusLocationModel>> call, Response<List<BusLocationModel>> response) {
                if (response.isSuccessful()) {
                    Log("BusLocationRepository", "getBusLocations", "response is successful: "+ response.toString());
                    List<BusLocationModel> busses = response.body();
                    //List<BusLocationModel> busLocations = response.body();
                    busLocationsLiveData.setValue(busses);
                } else {
                    Log("BusLocationRepository", "getBusLocations", "response is not successful: "+ response.toString());
                    ErrorResponse errorResponse;
                    if (response.code() == 404) {
                        errorResponse = new ErrorResponse("No buses available on the route currently.", response.code());
                    } else {
                        errorResponse = new ErrorResponse("Internal server error", response.code());
                    }
                    errorLiveData.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<List<BusLocationModel>> call, Throwable t) {
                Log("BusLocationRepository", "getBusLocations", "onFailure"+ t.getMessage());
                ErrorResponse errorResponse = new ErrorResponse("Network error: " + t.getMessage(), 503);
                errorLiveData.setValue(errorResponse);
            }
        });
    }

    public void startBusLocationUpdate(Set<String> busIds) {
        // Cancel any existing callbacks
        handler.removeCallbacks(updateBusLocationsRunnable);

        // Create a new Runnable to fetch bus locations
        updateBusLocationsRunnable = new Runnable() {
            @Override
            public void run() {
                fetchAndPostUpdatedBusLocations(busIds);
                //   checkIfBusPassedStop(routeId, busStopId);
                // Schedule the next update after 5 seconds
                handler.postDelayed(this, LOCATION_UPDATE_INTERVAL);
            }
        };

        // Post the initial update immediately
        handler.post(updateBusLocationsRunnable);
    }


    private void fetchAndPostUpdatedBusLocations(Set<String> busIds) {
        // Call your API to get the updated bus locations for the given busIds
        Call<List<BusLocationModel>> call = busApiService.getUpdatedBusLocations(busIds);

        call.enqueue(new Callback<List<BusLocationModel>>() {
            @Override
            public void onResponse(Call<List<BusLocationModel>> call, Response<List<BusLocationModel>> response) {
                if (response.isSuccessful()) {
                    List<BusLocationModel> updatedBusLocations = response.body();
                    // Update LiveData with the new bus locations
                    updatedBusLocationsLiveData.postValue(updatedBusLocations);
                } else {
                    ErrorResponse errorResponse;
                    if (response.code() == 404) {
                        errorResponse = new ErrorResponse("No buses available on the route currently.", response.code());
                    } else {
                        errorResponse = new ErrorResponse("Internal server error", response.code());
                    }
                    errorLiveData.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<List<BusLocationModel>> call, Throwable t) {
                ErrorResponse errorResponse = new ErrorResponse("Network error: " + t.getMessage(), 503);
                errorLiveData.setValue(errorResponse);
            }
        });
    }

    public void stopBusLocationUpdate() {
        // Remove any callbacks to stop the periodic updates
        handler.removeCallbacksAndMessages(null);
    }

    private void checkIfBusPassedStop(String  routeId, String busStopId) {

        Call<List<BusLocationModel>> call = busApiService.getBusesWithStatus(routeId, busStopId);

        call.enqueue(new Callback<List<BusLocationModel>>() {
            @Override
            public void onResponse(Call<List<BusLocationModel>> call, Response<List<BusLocationModel>> response) {
                if (response.isSuccessful()) {
                    List<BusLocationModel> updatedBusStatus = response.body();
                    updatedBusLocationsLiveData.postValue(updatedBusStatus);
                } else {
                    ErrorResponse errorResponse;
                    if (response.code() == 404) {
                        errorResponse = new ErrorResponse("No buses available on the route currently.", response.code());
                    } else {
                        errorResponse = new ErrorResponse("Internal server error", response.code());
                    }
                    errorLiveData.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<List<BusLocationModel>> call, Throwable t) {
                ErrorResponse errorResponse = new ErrorResponse("Network error: " + t.getMessage(), 503);
                errorLiveData.setValue(errorResponse);
            }
        });
    }

    public void resetData() {
        // Clear the data when necessary
        busLocationsLiveData.setValue(Collections.emptyList());
        updatedBusLocationsLiveData.setValue(Collections.emptyList());

        // Remove any callbacks to stop the periodic updates
        handler.removeCallbacks(updateBusLocationsRunnable);
    }
}
