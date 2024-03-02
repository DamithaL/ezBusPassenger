package ezbus.mit20550588.passenger.data.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ezbus.mit20550588.passenger.data.model.TicketOrder;
import ezbus.mit20550588.passenger.data.network.ApiServiceBus;
import ezbus.mit20550588.passenger.data.network.RetrofitClient;
import ezbus.mit20550588.passenger.data.repository.TicketRepository;

public class TicketViewModel extends ViewModel {

    private TicketRepository ticketRepository;
        private MutableLiveData<Double> fareLiveData = new MutableLiveData<>();
    private LiveData<String> errorLiveData;

    private LiveData<TicketOrder> ticketOrderLiveData;

    public TicketViewModel(TicketRepository ticketRepository, MutableLiveData<Double> fareLiveData, LiveData<String> errorLiveData) {
        this.ticketRepository = new TicketRepository(new RetrofitClient().getClient().create(ApiServiceBus.class));
        this.fareLiveData = fareLiveData;
        this.errorLiveData = errorLiveData;
    }

    // Constructor to initialize the repository
    public TicketViewModel() {
        this.ticketRepository = new TicketRepository(new RetrofitClient().getClient().create(ApiServiceBus.class));
        this.fareLiveData = new MutableLiveData<>();
        this.fareLiveData = ticketRepository.getFarePriceLiveData();
        this.errorLiveData = ticketRepository.getErrorLiveData();
    }



    // Existing constructor for dependency injection
    public TicketViewModel(TicketRepository paymentRepository) {
        this.ticketRepository = paymentRepository;
        this.fareLiveData = paymentRepository.getFarePriceLiveData();
        this.errorLiveData = paymentRepository.getErrorLiveData();
    }

    public LiveData<Double> getFarePriceLiveData() {
        return fareLiveData;
    }

    public LiveData<TicketOrder> getTicketOrderLiveData() {
        return ticketOrderLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void getFarePrice(String routeNumber, String routeName, String startBusStop, String endBusStop) {
        fareLiveData.setValue(null);
        ticketRepository.getFarePrice(routeNumber, routeName, startBusStop, endBusStop);
    }


}
