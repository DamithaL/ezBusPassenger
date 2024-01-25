package ezbus.mit20550588.manager.data.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class BusModel {

    private LatLng location;
    private String busId;
    private String busNumber;
    private String routeId;
    private String busColor;

    private boolean hasBusPassedBusStop;

    private Date lastUpdatedTime;

    public BusModel(String busId, String busNumber, double latitude, double longitude, String routeId, String busColor, LatLng location, boolean hasBusPassedBusStop) {
        this.busId = busId;
        this.busNumber = busNumber;
        this.routeId = routeId;
        this.busColor = busColor;
        this.location = location;
        this.hasBusPassedBusStop = hasBusPassedBusStop;
       
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

    public boolean hasBusPassedBusStop() {
        return hasBusPassedBusStop;
    }

    @Override
    public String toString() {
        return "BusModel{" +
                "location=" + location +
                ", busId='" + busId + '\'' +
                ", busNumber='" + busNumber + '\'' +
                ", routeId='" + routeId + '\'' +
                ", busColor='" + busColor + '\'' +
                ", hasBusPassedBusStop=" + hasBusPassedBusStop +
                ", lastUpdatedTime=" + lastUpdatedTime +
                '}';
    }
}
