package ezbus.mit20550588.passenger.data.remote;

import java.util.List;
import java.util.Set;

import ezbus.mit20550588.passenger.data.model.BusLocationModel;
import ezbus.mit20550588.passenger.data.model.BusModel;
import ezbus.mit20550588.passenger.data.model.UserModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiServiceBus {

    // Base_URL is in RetrofitClient class

    // endpoint to get bus locations by route
    @GET("location/get-location/routeId/{routeId}")
    Call<List<BusModel>> getBuses(@Path("routeId") String routeId);

    // endpoint to get updated bus locations by a list of bus IDs
    @POST("location/get-location/get-updated-locations")
    Call<List<BusModel>> getUpdatedBusLocations(@Body Set<String> busIds);

}
