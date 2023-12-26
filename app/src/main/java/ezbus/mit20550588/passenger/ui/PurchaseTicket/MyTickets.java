package ezbus.mit20550588.passenger.ui.PurchaseTicket;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static ezbus.mit20550588.passenger.util.Constants.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.libraries.places.api.Places;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.data.dao.PurchasedTicketDao;
import ezbus.mit20550588.passenger.data.database.AppDatabase;
import ezbus.mit20550588.passenger.data.model.PurchasedTicketModel;
import ezbus.mit20550588.passenger.data.model.RecentSearchModel;
import ezbus.mit20550588.passenger.data.model.TicketModel;
import ezbus.mit20550588.passenger.data.viewModel.PurchasedTicketViewModel;
import ezbus.mit20550588.passenger.data.viewModel.RecentSearchViewModel;
import ezbus.mit20550588.passenger.ui.adapters.NewTicketAdapter;
import ezbus.mit20550588.passenger.ui.adapters.PlacesAutoCompleteAdapter;
import ezbus.mit20550588.passenger.ui.adapters.RecentSearchAdapter;
import ezbus.mit20550588.passenger.util.DateUtils;

public class MyTickets extends AppCompatActivity {
    private RecyclerView recyclerViewForNewTickets;

    private PurchasedTicketViewModel purchasedTicketViewModel;
    private NewTicketAdapter newTicketAdapter;
    PurchasedTicketDao purchasedTicketDao;
    private PurchasedTicketModel purchasedTicketModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        // Back button
        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the current activity
                finish();
            }
        });

        RecyclerView recyclerViewForNewTickets = (RecyclerView) findViewById(R.id.newTickets_recycler_view);
        recyclerViewForNewTickets.setLayoutManager(new LinearLayoutManager(this));
        //  recyclerViewForNewTickets.setHasFixedSize(true);

        newTicketAdapter = new NewTicketAdapter();

        recyclerViewForNewTickets.setAdapter(newTicketAdapter);
        newTicketAdapter.setRecyclerView(recyclerViewForNewTickets);
        purchasedTicketViewModel = new ViewModelProvider(this).get(PurchasedTicketViewModel.class);
        purchasedTicketViewModel.getPurchasedTickets().observe(this, new Observer<List<PurchasedTicketModel>>() {
            @Override
            public void onChanged(List<PurchasedTicketModel> purchasedTicketModels) {
                // update RecyclerView
                Log("MyTicketsActivity", "Observe live data", "onChanged");

                newTicketAdapter.submitList(purchasedTicketModels);
               newTicketAdapter.scrollToTop();
            }
        });

        TextView newTicketCountText = findViewById(R.id.countOfNewTicketTextView);
        purchasedTicketViewModel.getCountOfNewTickets().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                String newText = "You have "+ integer + " new tickets available to redeem";
                newTicketCountText.setText(newText);
            }
        });

        // Set the item click listener
//        newTicketAdapter.setOnItemClickListener(recentSearch -> {
//            // Handle item click, e.g., show the location on the map
//          //  showLocationOfRecentSearch(recentSearch);
//
////            updateSearchDate(recentSearch);
//        });
        Log("MyTicketsActivity", "initialised");


    }
}