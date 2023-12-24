package ezbus.mit20550588.passenger.data.network;

import java.util.List;
import java.util.Set;


import ezbus.mit20550588.passenger.data.model.BusLocationModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiServiceBus {

    // Base_URL is in RetrofitClient class

//    // endpoint to get bus locations by route
//    @GET("location/get-location/routeId/{routeId}")
//    Call<List<BusModel>> getBuses(@Path("routeId") String routeId);
//
//    // endpoint to get updated bus locations by a list of bus IDs
//    @POST("location/get-location/get-updated-locations")
//    Call<List<BusModel>> getUpdatedBusLocations(@Body Set<String> busIds);
//
//    // endpoint to get the busses that has passed the bus stop or not
//    @GET("is-bus-gone/{routeId}/{selectedStopId}")
//    Call<List<BusModel>> getBusesWithStatus(
//            @Path("routeId") String routeId,
//            @Path("selectedStopId") String selectedStopId
//    );


    // endpoint to get bus locations by route
    @GET("location/get-location/routeId/{routeId}")
    Call<List<BusLocationModel>> getBuses(@Path("routeId") String routeId);

    // endpoint to get updated bus locations by a list of bus IDs
    @POST("location/get-location/get-updated-locations")
    Call<List<BusLocationModel>> getUpdatedBusLocations(@Body Set<String> busIds);

    // endpoint to get the busses that has passed the bus stop or not
    @GET("is-bus-gone/{routeId}/{selectedStopId}")
    Call<List<BusLocationModel>> getBusesWithStatus(
            @Path("routeId") String routeId,
            @Path("selectedStopId") String selectedStopId
    );

    // endpoint to get the fare
    @GET("fareCalculator/get-fare/{routeId}/{startStopName}/{endStopName}")
    Call<String> getFare(
            @Path("routeId") String routeId,
            @Path("startStopName") String startStopName,
            @Path("endStopName") String endStopName
    );
}
