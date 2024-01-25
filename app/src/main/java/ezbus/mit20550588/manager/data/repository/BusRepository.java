package ezbus.mit20550588.manager.data.repository;

import static ezbus.mit20550588.manager.util.Constants.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import ezbus.mit20550588.manager.data.network.ApiBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusRepository {

    private ApiBus apiService;

    private MutableLiveData<List<String>> routeNamesLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    public BusRepository(ApiBus apiService) {
        this.apiService = apiService;
    }

    public LiveData<List<String>> getrouteNamesLiveData () {
        return routeNamesLiveData;
    }

    public MutableLiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public LiveData<List<String>> fetchRouteNames() {
        MutableLiveData<List<String>> routesLiveData = new MutableLiveData<>();

        apiService.getRouteNames().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    routeNamesLiveData.setValue(response.body());
                    Log("Bus repository", "fetchRouteNames", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                String errorMessage = "Network failure. " + t.getMessage();
                errorMessageLiveData.setValue(errorMessage);
                Log("Bus repository", "fetchRouteNames: ERROR", t.getMessage());
            }
        });

        return routesLiveData;
    }

}
