package ezbus.mit20550588.passenger.ui.Login;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/auth/login")
    Call<LoginResult> executeLogin(@Body HashMap<String, String> map);


    @POST("/auth/signup")
    Call<Void> executeSignup (@Body HashMap<String, String> map);
}
