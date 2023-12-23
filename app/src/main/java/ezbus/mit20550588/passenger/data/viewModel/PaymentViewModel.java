package ezbus.mit20550588.passenger.data.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import ezbus.mit20550588.passenger.data.remote.ApiServiceBus;
import ezbus.mit20550588.passenger.data.remote.RetrofitClient;
import ezbus.mit20550588.passenger.data.repository.PaymentRepository;

public class PaymentViewModel extends ViewModel {

    private PaymentRepository paymentRepository;
    private LiveData<Double> fareLiveData;
    private LiveData<String> errorLiveData;

    public PaymentViewModel(PaymentRepository paymentRepository, LiveData<Double> fareLiveData, LiveData<String> errorLiveData) {
        this.paymentRepository = new PaymentRepository(new RetrofitClient().getClient().create(ApiServiceBus.class));
        this.fareLiveData = fareLiveData;
        this.errorLiveData = errorLiveData;
    }

    // Constructor to initialize the repository
    public PaymentViewModel() {
        this.paymentRepository = new PaymentRepository(new RetrofitClient().getClient().create(ApiServiceBus.class));
        this.fareLiveData = paymentRepository.getFarePriceLiveData();
        this.errorLiveData = paymentRepository.getErrorLiveData();
    }

    // Existing constructor for dependency injection
    public PaymentViewModel(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        this.fareLiveData = paymentRepository.getFarePriceLiveData();
        this.errorLiveData = paymentRepository.getErrorLiveData();
    }

    public LiveData<Double> getFarePriceLiveData() {
        return fareLiveData;
    }


    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void getFarePrice(String routeId, String startBusStop, String endBusStop) {
        paymentRepository.getFarePrice(routeId, startBusStop, endBusStop);
    }


}
