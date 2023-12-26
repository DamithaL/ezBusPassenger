package ezbus.mit20550588.passenger.ui.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ezbus.mit20550588.passenger.R;

public class NewTicketViewHolder extends RecyclerView.ViewHolder {

    TextView routeNameNumberTextView;
    TextView fromTextView;
    TextView toTextView;
    TextView purchasedDateTextView;
    TextView ticketPriceTextView;
    Button redeemButton;

    public NewTicketViewHolder(@NonNull View itemView) {
        super(itemView);
        // Initialize Type1ViewHolder views
        routeNameNumberTextView = itemView.findViewById(R.id.routeNameNumberTextView);
        fromTextView = itemView.findViewById(R.id.fromTextView);
        toTextView = itemView.findViewById(R.id.toTextView);
        purchasedDateTextView = itemView.findViewById(R.id.purchasedDateTextView);
        ticketPriceTextView = itemView.findViewById(R.id.ticketPriceTextView);
        redeemButton = itemView.findViewById(R.id.redeemButton);


    }
}

class UsedTicketViewHolder extends RecyclerView.ViewHolder {
    TextView routeNameNumberTextView;
    TextView fromTextView;
    TextView toTextView;
    TextView purchasedDateTextView;
    TextView ticketPriceTextView;
    TextView redeemedDateTextView;


    public UsedTicketViewHolder(@NonNull View itemView) {
        super(itemView);
        // Initialize Type2ViewHolder views
        routeNameNumberTextView = itemView.findViewById(R.id.routeNameNumberTextView);
        fromTextView = itemView.findViewById(R.id.fromTextView);
        toTextView = itemView.findViewById(R.id.toTextView);
        purchasedDateTextView = itemView.findViewById(R.id.purchasedDateTextView);
        ticketPriceTextView = itemView.findViewById(R.id.ticketPriceTextView);
        redeemedDateTextView = itemView.findViewById(R.id.redeemedDateTextView);
    }
}