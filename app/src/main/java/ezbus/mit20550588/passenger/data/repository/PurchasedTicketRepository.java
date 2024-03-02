package ezbus.mit20550588.passenger.data.repository;

import static ezbus.mit20550588.passenger.util.Constants.Log;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ezbus.mit20550588.passenger.data.dao.PurchasedTicketDao;
import ezbus.mit20550588.passenger.data.database.AppDatabase;
import ezbus.mit20550588.passenger.data.model.PurchasedTicketModel;
import ezbus.mit20550588.passenger.data.model.RecentSearchModel;
import ezbus.mit20550588.passenger.data.network.ApiServicePayment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchasedTicketRepository {
    private PurchasedTicketDao purchasedTicketDao;
    private ApiServicePayment apiService;
    private LiveData<List<PurchasedTicketModel>> allPurchasedTickets;

    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> isNetworkOkay = new MutableLiveData<>();


    public PurchasedTicketRepository(Application application, ApiServicePayment apiService) {
        Log("PurchasedTicketRepository", "1");
        AppDatabase database = AppDatabase.getInstance(application);
        purchasedTicketDao = database.purchasedTicketDao();
        allPurchasedTickets = purchasedTicketDao.getAllPurchasedTickets();
        this.apiService = apiService;
    }

    public  LiveData<Integer> getCountOfNewTickets(){
        return purchasedTicketDao.getCountOfNewTickets();
    }

    public MutableLiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public MutableLiveData<Boolean> getIsNetworkOkay() {
        return isNetworkOkay;
    }
    public void insert(PurchasedTicketModel purchasedTicket) {
        Log("PurchasedTicketRepository", "2");
        InsertAsyncTask insertTask = new InsertAsyncTask(purchasedTicketDao);
        insertTask.performBackgroundTask(purchasedTicket);
    }

    public LiveData<List<PurchasedTicketModel>> getAllPurchasedTickets() {
        Log("PurchasedTicketRepository", "3");
        return allPurchasedTickets;
    }

    // Method to update the date of a purchased ticket
    public void updatePurchasedTicket(PurchasedTicketModel purchasedTicket, Date redeemedDate) {
        Log("PurchasedTicketRepository", "4");
        UpdateAsyncTask updateTask = new UpdateAsyncTask(purchasedTicketDao);
        updateTask.performBackgroundTask(redeemedDate, purchasedTicket);

    }

    private static class InsertAsyncTask {

        private Executor executor = Executors.newSingleThreadExecutor();
        private PurchasedTicketDao purchasedTicketDao;

        public InsertAsyncTask(PurchasedTicketDao purchasedTicketDao) {
            Log("PurchasedTicketRepository", "5");
            this.purchasedTicketDao = purchasedTicketDao;
        }

        public void performBackgroundTask(PurchasedTicketModel... purchasedTicket) {
            Log("PurchasedTicketRepository", "5");
            executor.execute(() -> {
                if (purchasedTicket.length > 0) {
                    PurchasedTicketModel ticket = purchasedTicket[0];

                    // Insert the purchased ticket
                    purchasedTicketDao.insertPurchasedTicket(ticket);
                    Log("PurchasedTicketRepository", "InsertAsyncTask", "Purchased ticket saved");

                    // Add any additional logic you may need
                }
            });
        }
    }

    private static class UpdateAsyncTask {
        private Executor executor = Executors.newSingleThreadExecutor();
        private PurchasedTicketDao purchasedTicketDao;



        public UpdateAsyncTask(PurchasedTicketDao purchasedTicketDao) {
            Log("PurchasedTicketRepository", "7");
            this.purchasedTicketDao = purchasedTicketDao;
        }

        public void performBackgroundTask(Date redeemedDate, PurchasedTicketModel... purchasedTicket) {
            executor.execute(() -> {
                Log("PurchasedTicketRepository", "8");
                if (purchasedTicket.length > 0) {
                    PurchasedTicketModel ticket = purchasedTicket[0];
                    ticket.setRedeemed(true);
                    ticket.setRedeemedDate(redeemedDate);
                    purchasedTicketDao.updatePurchasedTicket(ticket);
                }
            });
        }
    }




    public void fetchAllPurchasedTickets(String email) {

        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        apiService.fetchAllPurchasedTickets(request).enqueue(new Callback<List<PurchasedTicketModel>>() {
            @Override
            public void onResponse(Call<List<PurchasedTicketModel>> call, Response<List<PurchasedTicketModel>> response) {
                if (response.isSuccessful()) {
                    try { // Update local database with the received data
                        updateLocalDatabase(response.body());
                       // allBusAccountsLiveData.setValue(response.body());

                        errorMessageLiveData.setValue("");
                        Log("Purchased Ticket repository", "fetchAllPurchasedTickets", response.body().toString());
                    } catch (Exception e) {
                        errorMessageLiveData.setValue(e.getMessage());
                        isNetworkOkay.setValue(false);
                        Log("Purchased Ticket repository", "fetchAllPurchasedTickets", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PurchasedTicketModel>> call, Throwable t) {
                String errorMessage = "Network failure. " + t.getMessage();
                errorMessageLiveData.setValue(errorMessage);
                Log("Purchased Ticket repository", "fetchAllPurchasedTickets: ERROR", t.getMessage());
                isNetworkOkay.setValue(false);
            }
        });

    }

    // asycn task to update the local database
    private void updateLocalDatabase(List<PurchasedTicketModel> purchasedTicketList) {
        Log("Purchased Ticket repository", "updateLocalDatabase", "Updating local database");
        UpdatePurchaseTicketsAsyncTask updateTask = new UpdatePurchaseTicketsAsyncTask(purchasedTicketDao);
        updateTask.performBackgroundTask(purchasedTicketList);
    }

    private static class UpdatePurchaseTicketsAsyncTask {

        private Executor executor = Executors.newSingleThreadExecutor();
        private PurchasedTicketDao purchasedTicketDao ;

        public UpdatePurchaseTicketsAsyncTask(PurchasedTicketDao purchasedTicketDao) {
            this.purchasedTicketDao = purchasedTicketDao;
        }

        public void performBackgroundTask(List<PurchasedTicketModel> purchasedTicketList) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (purchasedTicketList != null && !purchasedTicketList.isEmpty()) {
                        purchasedTicketDao.deleteAllPurchasedTickets();
                        purchasedTicketDao.insertPurchasedTickets(purchasedTicketList);
                    }
                }
            });
        }
    }
}
