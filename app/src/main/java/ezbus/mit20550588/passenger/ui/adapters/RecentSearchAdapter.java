package ezbus.mit20550588.passenger.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static ezbus.mit20550588.passenger.util.Constants.Log;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.data.model.RecentSearchModel;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.ViewHolder> {
    private List<RecentSearchModel> recentSearches = new ArrayList<>();
    // Add a listener member variable and a method to set the listener
    private OnItemClickListener listener;
    private RecyclerView recyclerView;


    // onCreateViewHolder to inflate the item layout and create the ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_search_recycler_layout, parent, false);
        return new ViewHolder(itemView);
    }

    // onBindViewHolder to bind the data to the views
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentSearchModel currentSearch = recentSearches.get(position);
        holder.textViewPlaceName.setText(currentSearch.getLocationName());
    }

    // getItemCount to get the number of items in the data set
    @Override
    public int getItemCount() {
        return recentSearches.size();
    }

    // Constructor to initialize the data and the recyclerView reference
    public RecentSearchAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    // Method to set recent searches and scroll to the top
    public void setRecentSearches(List<RecentSearchModel> recentSearches) {
        this.recentSearches = recentSearches;
        notifyDataSetChanged();

        // Scroll to the top after updating the data
        try {
            if (recyclerView != null) {
                recyclerView.smoothScrollToPosition(0);
            }
        } catch (Exception e) {
            Log("setRecentSearches", "ERROR", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // ViewHolder class to hold the views
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewPlaceName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPlaceName = itemView.findViewById(R.id.place_area);

            // Set click listener for the item view
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(recentSearches.get(position));
                }
            });

        }
    }

    // interface for item click events
    public interface OnItemClickListener {
        void onItemClick(RecentSearchModel recentSearch);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
