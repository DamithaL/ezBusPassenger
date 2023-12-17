package ezbus.mit20550588.passenger.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static ezbus.mit20550588.passenger.util.Constants.Log;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.data.model.RecentSearchModel;

public class RecentSearchAdapter extends ListAdapter<RecentSearchModel, RecentSearchAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<RecentSearchModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<RecentSearchModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull RecentSearchModel oldItem, @NonNull RecentSearchModel newItem) {
            return oldItem.getSearchId() == newItem.getSearchId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull RecentSearchModel oldItem, @NonNull RecentSearchModel newItem) {
            return oldItem.getLocationLatLang().equals(newItem.getLocationLatLang()) &&
                    oldItem.getLocationName().equals(newItem.getLocationName()) &&
                    oldItem.getSearchDate().equals(newItem.getSearchDate());

        }
    };

    // Add a listener member variable and a method to set the listener
    private OnItemClickListener listener;
    private RecyclerView recyclerView;

    public RecentSearchAdapter() {
        super(DIFF_CALLBACK);
    }

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
        RecentSearchModel currentSearch = getItem(position);
        holder.textViewPlaceName.setText(currentSearch.getLocationName());
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
                    listener.onItemClick(getItem(position));
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

    // Method to set recent searches and scroll to the top
    public void scrollToTop() {
        if (recyclerView != null && getItemCount() > 0) {
            recyclerView.smoothScrollToPosition(0);
            Log("RecentSearchAdapter","scrollToTop", "Done");
        }
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
}


