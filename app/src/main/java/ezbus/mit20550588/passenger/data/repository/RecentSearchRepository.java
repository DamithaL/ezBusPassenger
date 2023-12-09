package ezbus.mit20550588.passenger.data.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ezbus.mit20550588.passenger.data.dao.RecentSearchDao;
import ezbus.mit20550588.passenger.data.database.AppDatabase;
import ezbus.mit20550588.passenger.data.model.RecentSearchModel;

import static ezbus.mit20550588.passenger.util.Constants.Log;

public class RecentSearchRepository {
    private RecentSearchDao recentSearchDao;
    private LiveData<List<RecentSearchModel>> allRecentSearches;


    public RecentSearchRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        recentSearchDao = database.recentSearchDao();
        allRecentSearches = recentSearchDao.getRecentSearches();
    }

    public void insert(RecentSearchModel recentSearch) {
        InsertAsyncTask insertTask = new InsertAsyncTask(recentSearchDao);
        insertTask.performBackgroundTask(recentSearch);
    }

    public LiveData<List<RecentSearchModel>> getAllRecentSearches() {
        return allRecentSearches;
    }

    // Method to update the date of a recent search
    public void updateRecentSearchDate(RecentSearchModel recentSearch) {
        UpdateAsyncTask updateTask = new UpdateAsyncTask(recentSearchDao);
        updateTask.performBackgroundTask(recentSearch);
    }

    private static class InsertAsyncTask {
        private Executor executor = Executors.newSingleThreadExecutor();
        private RecentSearchDao recentSearchDao;
        private static final int MAX_RECENT_SEARCHES = 30;


        public InsertAsyncTask(RecentSearchDao recentSearchDao) {
            this.recentSearchDao = recentSearchDao;
        }

        public void performBackgroundTask(RecentSearchModel... recentSearch) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (recentSearch.length > 0) {
                        RecentSearchModel search = recentSearch[0];

                        // Check if the search already exists
                        RecentSearchModel existingSearch = recentSearchDao.getRecentSearchByName(search.getLocationName());

                        if (existingSearch == null) {
                            // If it doesn't exist, insert as a new search
                            recentSearchDao.insertRecentSearch(search);
                            Log("RecentSearchRepository", "InsertAsyncTask","New entry found, saved to recent searches");
                        } else {
                            // If it exists, update the search date
                            Date currentDate = new Date();
                            existingSearch.setSearchDate(currentDate);
                            recentSearchDao.updateRecentSearch(existingSearch);
                            Log("RecentSearchRepository", "InsertAsyncTask","Location is already exist in recent searches");

                        }


                        int currentCount = recentSearchDao.getRecentSearchesCount();

                        // Set a threshold, delete oldest 10 when the count reaches 30
                        if (currentCount > MAX_RECENT_SEARCHES) {

                            // Get the oldest searches and then delete them using @Delete
                            List<RecentSearchModel> oldestSearches = recentSearchDao.getOldestSearches();
                            if (oldestSearches != null && oldestSearches.size() > 0) {
                                recentSearchDao.deleteOldestSearches(oldestSearches.toArray(new RecentSearchModel[0]));
                                Log("RecentSearchRepository", "InsertAsyncTask","Recent searches count maxed. Deleted oldest searches");

                            }
                        }
                    }
                }
            });
        }

    }

    private static class UpdateAsyncTask {
        private Executor executor = Executors.newSingleThreadExecutor();
        private RecentSearchDao recentSearchDao;


        public UpdateAsyncTask(RecentSearchDao recentSearchDao) {
            this.recentSearchDao = recentSearchDao;
        }

        public void performBackgroundTask(RecentSearchModel... recentSearch) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (recentSearch.length > 0) {
                        recentSearchDao.updateRecentSearch(recentSearch[0]);
                    }
                }
            });
        }

    }

}
