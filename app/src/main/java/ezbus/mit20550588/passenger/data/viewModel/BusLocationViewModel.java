package ezbus.mit20550588.passenger.data.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Set;


import ezbus.mit20550588.passenger.data.model.BusLocationModel;
import ezbus.mit20550588.passenger.data.network.ApiServiceBus;
import ezbus.mit20550588.passenger.data.network.ErrorResponse;
import ezbus.mit20550588.passenger.data.network.RetrofitClient;
import ezbus.mit20550588.passenger.data.repository.BusLocationRepository;

public class BusLocationViewModel extends ViewModel {

    private BusLocationRepository busLocationRepository;
    private LiveData<List<BusLocationModel>> busLocationsLiveData;
    private LiveData<List<BusLocationModel>> updatedBusLocationsLiveData;
    private LiveData<ErrorResponse> errorLiveData;


    // Constructor to initialize the repository
    public BusLocationViewModel() {
        this.busLocationRepository = new BusLocationRepository(new RetrofitClient().getClient().create(ApiServiceBus.class));
        this.busLocationsLiveData = busLocationRepository.getBusLocationsLiveData();
        this.updatedBusLocationsLiveData = busLocationRepository.getUpdatedBusLocationsLiveData();
        this.errorLiveData = busLocationRepository.getErrorLiveData();
    }

    // Existing constructor for dependency injection
    public BusLocationViewModel(BusLocationRepository busLocationRepository) {
        this.busLocationRepository = busLocationRepository;
        this.busLocationsLiveData = busLocationRepository.getBusLocationsLiveData();
        this.updatedBusLocationsLiveData = busLocationRepository.getUpdatedBusLocationsLiveData();
        this.errorLiveData = busLocationRepository.getErrorLiveData();
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
        busLocationRepository.getBusLocations(routeNumber, routeName);
    }

    public void updateBusLocations(Set<String> busIds) {
        busLocationRepository.startBusLocationUpdate(busIds
        );
    }

    public void stopBusLocationUpdate() {
        busLocationRepository.stopBusLocationUpdate();
    }

    // Expose a method to reset data in the ViewModel
    public void resetData() {
        busLocationRepository.resetData();
    }


}
