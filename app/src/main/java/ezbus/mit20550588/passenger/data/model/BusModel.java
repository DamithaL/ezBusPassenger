package ezbus.mit20550588.passenger.data.model;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class BusModel {

    private LatLng location;
    private String busId;
    private String busNumber;
    private String routeId;
    private String busColor;

    private Date lastUpdatedTime;

    public BusModel(String busId, String busNumber, double latitude, double longitude, String routeId, String busColor, LatLng location) {
        this.busId = busId;
        this.busNumber = busNumber;
        this.routeId = routeId;
        this.busColor = busColor;
        this.location = location;
       
    }

    public String getBusId() {
        return busId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getBusColor() {
        return busColor;
    }

    public LatLng getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "BusModel{" +
                "location=" + location +
                ", busId='" + busId + '\'' +
                ", busNumber='" + busNumber + '\'' +
                ", routeId='" + routeId + '\'' +
                ", busColor='" + busColor + '\'' +
                ", lastUpdatedTime=" + lastUpdatedTime +
                '}';
    }
}
