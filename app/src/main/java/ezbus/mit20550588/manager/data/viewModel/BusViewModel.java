package ezbus.mit20550588.manager.data.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.Closeable;
import java.util.List;

import ezbus.mit20550588.manager.data.network.ApiBus;
import ezbus.mit20550588.manager.data.network.RetrofitClient;
import ezbus.mit20550588.manager.data.repository.BusRepository;
import ezbus.mit20550588.manager.data.repository.FleetRepository;

public class BusViewModel extends ViewModel {
    private final BusRepository busRepository;
    private final LiveData<List<String>> routesNamesLiveData;
    private MutableLiveData<String> errorMessageLiveData;

    public BusViewModel() {
        this.busRepository = new BusRepository(new RetrofitClient().getClient().create(ApiBus.class));
        this.routesNamesLiveData = busRepository.getrouteNamesLiveData();
        this.errorMessageLiveData = busRepository.getErrorMessageLiveData();
    }

    // Existing constructor for dependency injection
    public BusViewModel(BusRepository busRepository) {
        this.busRepository = busRepository;
        this.routesNamesLiveData = busRepository.getrouteNamesLiveData();
        this.errorMessageLiveData = busRepository.getErrorMessageLiveData();
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public LiveData<List<String>> getRouteNamesLiveData() {
        return routesNamesLiveData;
    }

    public void fetchRouteNames() {
        busRepository.fetchRouteNames();
    }

}
