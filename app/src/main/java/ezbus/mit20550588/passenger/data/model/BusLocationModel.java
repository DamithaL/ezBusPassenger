// THIS HAS BEEN TEMPORARY DEACTIVATED AND BUS LOCATION ALSO DEFINED IN BUS MODEL


package ezbus.mit20550588.passenger.data.model;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class BusLocationModel {
    private BusModel bus;
    private LatLng location;
    private Date timeStamp;

   // private Double farePrice;
   // private boolean hasBusPassedBusStop;

    public BusLocationModel(BusModel bus, LatLng location, Date timeStamp
         //  , boolean hasBusPassedBusStop,Double farePrice
    ) {
        this.bus = bus;
        this.location = location;
        this.timeStamp = timeStamp;
      //  this.hasBusPassedBusStop = hasBusPassedBusStop;
      //  this.farePrice = farePrice;

    }

    public BusLocationModel(){

    }

    public BusModel getBus() {
        return bus;
    }

    public void setBus(BusModel bus) {
        this.bus = bus;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

//    public boolean isBusPassedBusStop() {
//        return hasBusPassedBusStop;
//    }

//    public Double getBusFarePrice() {
//        return farePrice;
//    }

//    public void setBusFarePrice(Double farePrice) {
//        this.farePrice = farePrice;
//    }

    @Override
    public String toString() {
        return "BusLocationModel{" +
                "bus=" + bus +
                ", location=" + location +
                ", timeStamp=" + timeStamp +
             //   ", farePrice=" + farePrice +
              //  ", hasBusPassedBusStop=" + hasBusPassedBusStop +
                '}';
    }
}
