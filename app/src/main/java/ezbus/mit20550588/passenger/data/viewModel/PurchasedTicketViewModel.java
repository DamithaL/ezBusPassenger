package ezbus.mit20550588.passenger.data.viewModel;

import static ezbus.mit20550588.passenger.util.Constants.Log;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;
import java.util.List;

import ezbus.mit20550588.passenger.data.model.PurchasedTicketModel;
import ezbus.mit20550588.passenger.data.network.ApiServicePayment;
import ezbus.mit20550588.passenger.data.network.RetrofitClient;
import ezbus.mit20550588.passenger.data.repository.PurchasedTicketRepository;

public class PurchasedTicketViewModel extends AndroidViewModel {

    private PurchasedTicketRepository repository;
    private LiveData<List<PurchasedTicketModel>> allPurchasedTickets;
    private MutableLiveData<String> errorMessageLiveData;
    private LiveData<Boolean> isNetworkOkay;

    public PurchasedTicketViewModel(@NonNull Application application) {
        super(application);
        this.repository = new PurchasedTicketRepository(application, new RetrofitClient().getClient().create(ApiServicePayment.class));
        this.allPurchasedTickets = repository.getAllPurchasedTickets();
        this.isNetworkOkay = repository.getIsNetworkOkay();
        this.errorMessageLiveData = repository.getErrorMessageLiveData();

        Log("PurchasedTicketViewModel", "1");
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public LiveData<Boolean> getIsNetworkOkay() {
        return isNetworkOkay;
    }

    public void insert(PurchasedTicketModel purchasedTicket) {
        Log("PurchasedTicketViewModel", "2");
        repository.insert(purchasedTicket);

    }

    public  LiveData<Integer> getCountOfNewTickets(){
        return repository.getCountOfNewTickets();
    }

    public LiveData<List<PurchasedTicketModel>> getPurchasedTickets() {
        Log("PurchasedTicketViewModel", "3");
        return allPurchasedTickets;
    }


    public void fetchAllPurchasedTickets(String email) {
        repository.fetchAllPurchasedTickets(email);
    }

    // Update ticket when the server return it as redeemed
    public void update(PurchasedTicketModel purchasedTicket, Date redeemedDate) {
        repository.updatePurchasedTicket(purchasedTicket, redeemedDate);
    }
}
