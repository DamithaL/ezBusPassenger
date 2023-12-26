package ezbus.mit20550588.passenger.data.repository;

import static ezbus.mit20550588.passenger.util.Constants.Log;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ezbus.mit20550588.passenger.data.dao.PurchasedTicketDao;
import ezbus.mit20550588.passenger.data.database.AppDatabase;
import ezbus.mit20550588.passenger.data.model.PurchasedTicketModel;

public class PurchasedTicketRepository {
    private PurchasedTicketDao purchasedTicketDao;
    private LiveData<List<PurchasedTicketModel>> allPurchasedTickets;


    public PurchasedTicketRepository(Application application) {
        Log("PurchasedTicketRepository", "1");
        AppDatabase database = AppDatabase.getInstance(application);
        purchasedTicketDao = database.purchasedTicketDao();
        allPurchasedTickets = purchasedTicketDao.getAllPurchasedTickets();
    }

    public  LiveData<Integer> getCountOfNewTickets(){
        return purchasedTicketDao.getCountOfNewTickets();
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
    public void updatePurchasedTicket(PurchasedTicketModel purchasedTicket) {
        Log("PurchasedTicketRepository", "4");
        UpdateAsyncTask updateTask = new UpdateAsyncTask(purchasedTicketDao);
        updateTask.performBackgroundTask(purchasedTicket);
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

        public void performBackgroundTask(PurchasedTicketModel... purchasedTicket) {
            executor.execute(() -> {
                Log("PurchasedTicketRepository", "8");
                if (purchasedTicket.length > 0) {
                    Log("PurchasedTicketRepository", "9");
                    purchasedTicketDao.updatePurchasedTicket(purchasedTicket[0]);
                }
            });
        }
    }
}
