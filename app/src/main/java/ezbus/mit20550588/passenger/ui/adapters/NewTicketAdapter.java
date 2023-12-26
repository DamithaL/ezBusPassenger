package ezbus.mit20550588.passenger.ui.adapters;

import static ezbus.mit20550588.passenger.util.Constants.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.data.model.PurchasedTicketModel;
import ezbus.mit20550588.passenger.data.model.RecentSearchModel;

public class NewTicketAdapter extends ListAdapter<PurchasedTicketModel, RecyclerView.ViewHolder> {

    private static final int NEW_TICKET_VIEW_TYPE_TYPE = 1;
    private static final int USED_TICKET_VIEW_TYPE_TYPE = 2;

    @Override
    public int getItemViewType(int position) {
        PurchasedTicketModel currentTicket = getItem(position);

        // Determine the condition based on the data at the position
        if (currentTicket.isRedeemed()) {
            return USED_TICKET_VIEW_TYPE_TYPE;
        } else {
            return NEW_TICKET_VIEW_TYPE_TYPE;
        }
    }

    private static final DiffUtil.ItemCallback<PurchasedTicketModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<PurchasedTicketModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull PurchasedTicketModel oldItem, @NonNull PurchasedTicketModel newItem) {
            return oldItem.getTicketId() == newItem.getTicketId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull PurchasedTicketModel oldItem, @NonNull PurchasedTicketModel newItem) {
            return oldItem.getOrderId().equals(newItem.getOrderId()) &&
                    oldItem.getPurchasedDate().equals(newItem.getPurchasedDate()) &&
                    oldItem.isRedeemed() == newItem.isRedeemed() &&
                    ((oldItem.getRedeemedDate() == null && newItem.getRedeemedDate() == null) ||
                            (oldItem.getRedeemedDate() != null && newItem.getRedeemedDate() != null &&
                                    oldItem.getRedeemedDate().equals(newItem.getRedeemedDate()))
                    ) &&
                    oldItem.getTicket().equals(newItem.getTicket());
        }
    };

    // Add a listener member variable and a method to set the listener
    private OnItemClickListener listener;
    private RecyclerView recyclerView;

    public NewTicketAdapter() {
        super(DIFF_CALLBACK);
    }

    // onCreateViewHolder to inflate the item layout and create the ViewHolder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());


        // Inflate different layouts based on view type
        if (viewType == NEW_TICKET_VIEW_TYPE_TYPE) {
            View view = inflater.inflate(R.layout.recyler_layout_new_tickets, parent, false);
            return new NewTicketViewHolder(view);
            //  return new ViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.recyler_layout_used_tickets, parent, false);
            return new UsedTicketViewHolder(view);
            // return new ViewHolder(view);
        }


        //   View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_layout_new_tickets, parent, false);
        // Log("NewTicketAdapter", "1");
        //return new ViewHolder(itemView);
    }

    // onBindViewHolder to bind the data to the views
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        PurchasedTicketModel currentTicket = getItem(position);
        String routeNameNumberText = currentTicket.getTicket().getRouteNumber() + " | " + currentTicket.getTicket().getRouteName();
        String fromText = "From: " + currentTicket.getTicket().getArrivalStopName();
        String toText = "To: " + currentTicket.getTicket().getDepartureStopName();
        String purchasedDateText = "Purchased on: " + currentTicket.getPurchasedDateAsString();
        String ticketPriceText = "Rs. " + currentTicket.getTicket().getFarePrice();
        String redeemedDateText = "on " + currentTicket.getRedeemedDateAsString();

        if (holder instanceof NewTicketViewHolder) {
            // Bind data for Type1ViewHolder
            NewTicketViewHolder newTicketViewHolder = (NewTicketViewHolder) holder;
            newTicketViewHolder.routeNameNumberTextView.setText(routeNameNumberText);
            newTicketViewHolder.fromTextView.setText(fromText);
            newTicketViewHolder.toTextView.setText(toText);
            newTicketViewHolder.purchasedDateTextView.setText(purchasedDateText);
            newTicketViewHolder.ticketPriceTextView.setText(ticketPriceText);
            newTicketViewHolder.redeemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


        } else if (holder instanceof UsedTicketViewHolder) {
            UsedTicketViewHolder usedTicketViewHolder = (UsedTicketViewHolder) holder;
            usedTicketViewHolder.routeNameNumberTextView.setText(routeNameNumberText);
            usedTicketViewHolder.fromTextView.setText(fromText);
            usedTicketViewHolder.toTextView.setText(toText);
            usedTicketViewHolder.purchasedDateTextView.setText(purchasedDateText);
            usedTicketViewHolder.redeemedDateTextView.setText(redeemedDateText);
            usedTicketViewHolder.ticketPriceTextView.setText(ticketPriceText);

        }


//        PurchasedTicketModel currentTicket = getItem(position);
//        holder.routeNameNumberText.setText(String.valueOf(currentTicket.getTicketId()));
//        Log("NewTicketAdapter", "2");
    }

    // ViewHolder class to hold the views
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView routeNameNumberText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            routeNameNumberText = itemView.findViewById(R.id.routeNameNumberTextView);

            // Set click listener for the item view
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getItem(position));
                    Log("NewTicketAdapter", "3");
                }
            });

        }
    }

    // interface for item click events
    public interface OnItemClickListener {

        void onItemClick(PurchasedTicketModel purchasedTicket);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Method to set recent searches and scroll to the top
    public void scrollToTop() {
        if (recyclerView != null && getItemCount() > 0) {
            recyclerView.smoothScrollToPosition(0);
            Log("RecentSearchAdapter", "scrollToTop", "Done");
        }
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
}
