package ezbus.mit20550588.passenger.data.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ezbus.mit20550588.passenger.data.dao.RecentSearchDao;
import ezbus.mit20550588.passenger.data.database.AppDatabase;
import ezbus.mit20550588.passenger.data.model.RecentSearchModel;

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
                        recentSearchDao.insertRecentSearch(recentSearch[0]);

                        int currentCount = recentSearchDao.getRecentSearchesCount();

                        // Set a threshold, delete oldest 10 when the count reaches 30
                        if (currentCount > MAX_RECENT_SEARCHES) {

                            // Get the oldest searches and then delete them using @Delete
                            List<RecentSearchModel> oldestSearches = recentSearchDao.getOldestSearches();
                            if (oldestSearches != null && oldestSearches.size() > 0) {
                                recentSearchDao.deleteOldestSearches(oldestSearches.toArray(new RecentSearchModel[0]));
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
