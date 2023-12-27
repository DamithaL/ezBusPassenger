package ezbus.mit20550588.passenger.data.network;

import java.util.Map;

import ezbus.mit20550588.passenger.data.model.UserModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiServiceAuthentication {

    // Base_URL is in RetrofitClient class

    @POST("/auth/login")
    Call<UserModel> loginUser(@Body LoginRequest loginRequest);


    @POST("/auth/signup")
    Call<Map<String, String>> registerUser (@Body RegistrationRequest registrationRequest);

    @POST("/auth/verify-user")
    Call<UserModel> verifyUser (@Body Map<String, String> email);

//
//    @POST("/auth/login")
//    Call<LoginResult> executeLogin(@Body HashMap<String, String> map);
//
//
//    @POST("/auth/signup")
//    Call<Void> executeSignup (@Body HashMap<String, String> map);
}


