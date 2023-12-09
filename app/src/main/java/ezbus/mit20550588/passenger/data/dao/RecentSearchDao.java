// Location: ezbus.mit20550588.passenger.data.dao

package ezbus.mit20550588.passenger.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import ezbus.mit20550588.passenger.data.model.RecentSearchModel;

@Dao
public interface RecentSearchDao {

    // Insert a new search
    @Insert
    void insertRecentSearch(RecentSearchModel recentSearchModel);

    // Get the list of most recent 20 searches
    @Query("SELECT * FROM recent_search_table ORDER BY searchDate DESC LIMIT 20")
    LiveData<List<RecentSearchModel>> getRecentSearches();

    // Get the count of all searches
    @Query("SELECT COUNT(*) FROM recent_search_table")
    int getRecentSearchesCount();

    // Get the list of 10 oldest searches
    @Query("SELECT * FROM recent_search_table ORDER BY searchDate ASC LIMIT 10")
    List<RecentSearchModel> getOldestSearches();

    // Delete a given search
    @Delete
    void deleteOldestSearches(RecentSearchModel... recentSearchModels);

    // Update query for updating the date of a recent search
    @Update
    void updateRecentSearch(RecentSearchModel recentSearch);

    @Query("SELECT * FROM recent_search_table WHERE locationName = :searchName LIMIT 1")
    RecentSearchModel getRecentSearchByName(String searchName);


}
