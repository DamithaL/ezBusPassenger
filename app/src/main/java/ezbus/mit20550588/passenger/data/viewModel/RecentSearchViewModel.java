// Usage in ViewModel

package ezbus.mit20550588.passenger.data.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import ezbus.mit20550588.passenger.data.database.AppDatabase;
import ezbus.mit20550588.passenger.data.model.RecentSearchModel;
import ezbus.mit20550588.passenger.data.repository.RecentSearchRepository;

public class RecentSearchViewModel extends AndroidViewModel {

    private RecentSearchRepository repository;
    private LiveData<List<RecentSearchModel>> allRecentSearches;

    public RecentSearchViewModel(@NonNull Application application) {
        super(application);
        repository = new RecentSearchRepository(application);
        allRecentSearches = repository.getAllRecentSearches();
    }

    public void insert(RecentSearchModel recentSearch) {
        repository.insert(recentSearch);
    }

    public LiveData<List<RecentSearchModel>> getRecentSearches() {
        return allRecentSearches;
    }

    public void update(RecentSearchModel recentSearch) {
        repository.updateRecentSearchDate(recentSearch);
    }
}
