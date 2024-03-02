package ezbus.mit20550588.passenger.data.model;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class BusModel {

    private String busRegNumber;
    private String busRouteNumber;
    private String busRouteName;
    private String busColor;

    public BusModel(String busRegNumber, String busRouteNumber, String busRouteName, String busColor) {
        this.busRegNumber = busRegNumber;
        this.busRouteNumber = busRouteNumber;
        this.busRouteName = busRouteName;
        this.busColor = busColor;
    }

    public String getBusRegNumber() {
        return busRegNumber;
    }

    public String getBusRouteNumber() {
        return busRouteNumber;
    }

    public String getBusRouteName() {
        return busRouteName;
    }

    public String getBusColor() {
        return busColor;
    }
}
