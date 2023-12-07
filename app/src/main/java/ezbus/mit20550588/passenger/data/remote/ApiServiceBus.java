package ezbus.mit20550588.passenger.data.remote;

import java.util.List;

import ezbus.mit20550588.passenger.data.model.BusModel;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiServiceBus {
    @GET("/bus-locations")
    Call<List<BusModel>> getBusLocations();
}
