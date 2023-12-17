package ezbus.mit20550588.passenger.data.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ezbus.mit20550588.passenger.data.model.BusLocationModel;
import ezbus.mit20550588.passenger.data.model.BusModel;
import ezbus.mit20550588.passenger.data.remote.ApiServiceAuthentication;
import ezbus.mit20550588.passenger.data.remote.ApiServiceBus;
import ezbus.mit20550588.passenger.data.remote.RetrofitClient;
import ezbus.mit20550588.passenger.data.repository.BusLocationRepository;
import ezbus.mit20550588.passenger.data.repository.RecentSearchRepository;
import ezbus.mit20550588.passenger.data.repository.UserRepository;

public class BusLocationViewModel extends ViewModel {

    private BusLocationRepository busLocationRepository;
    private LiveData<List<BusModel>> busLocationsLiveData;
    private LiveData<String> errorLiveData;

    // Constructor to initialize the repository
    public BusLocationViewModel() {
        this.busLocationRepository = new BusLocationRepository(new RetrofitClient().getClient().create(ApiServiceBus.class));
        this.busLocationsLiveData = busLocationRepository.getBusLocationsLiveData();
        this.errorLiveData = busLocationRepository.getErrorLiveData();
    }

    // Existing constructor for dependency injection
    public BusLocationViewModel(BusLocationRepository busLocationRepository) {
        this.busLocationRepository = busLocationRepository;
        this.busLocationsLiveData = busLocationRepository.getBusLocationsLiveData();
        this.errorLiveData = busLocationRepository.getErrorLiveData();
    }

    public LiveData<List<BusModel>> getBusLocationsLiveData() {
        return busLocationsLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void getBusLocations(String routeId) {
        busLocationRepository.getBusLocations(routeId);
    }

    // Expose a method to reset data in the ViewModel
    public void resetData() {
        busLocationRepository.resetData();
    }
}
