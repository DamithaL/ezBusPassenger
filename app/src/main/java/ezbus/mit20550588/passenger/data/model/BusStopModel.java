package ezbus.mit20550588.passenger.data.model;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class BusStopModel {
    private String id;
    private String name;
    private LatLng location;
    private String routeId;

    public BusStopModel(String id, String name, LatLng location, String routeId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.routeId = routeId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getRouteId() {
        return routeId;
    }

    @NonNull
    @Override
    public String toString() {
        return "BusStopModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", routeId='" + routeId + '\'' +
                '}';
    }
}

