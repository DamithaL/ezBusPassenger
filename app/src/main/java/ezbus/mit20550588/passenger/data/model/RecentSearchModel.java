// Location: ezbus.mit20550588.passenger.data.model

package ezbus.mit20550588.passenger.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

@Entity(tableName = "recent_search_table") // Default would be class name. But this is more SQL Lite convention
public class RecentSearchModel {

    @PrimaryKey(autoGenerate = true)
    private int searchId;

    private String locationName;
    private LatLng locationLatLang;
    private final Date searchDate;


    public int getSearchId() {
        return searchId;
    }

    public String getLocationName() {
        return locationName;
    }

    public LatLng getLocationLatLang() {
        return locationLatLang;
    }

    public Date getSearchDate() {
        return searchDate;
    }

    public RecentSearchModel(String locationName, LatLng locationLatLang, Date searchDate) {
        this.locationName = locationName;
        this.locationLatLang = locationLatLang;
        this.searchDate = searchDate;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }
}
