package ezbus.mit20550588.passenger.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;

import ezbus.mit20550588.passenger.data.model.UserModel;
import ezbus.mit20550588.passenger.data.network.ApiServiceAuthentication;
import ezbus.mit20550588.passenger.data.network.LoginRequest;
import ezbus.mit20550588.passenger.data.network.RegistrationRequest;
import ezbus.mit20550588.passenger.data.viewModel.AuthResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private ApiServiceAuthentication apiService;
    private MutableLiveData<AuthResult> authResultLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();


    public UserRepository(ApiServiceAuthentication apiService) {
        this.apiService = apiService;
    }

    public LiveData<AuthResult> getAuthResultLiveData() {
        return authResultLiveData;
    }
    public MutableLiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }



    public void loginUser(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        apiService.loginUser(loginRequest).enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful()) {
                    authResultLiveData.setValue(new AuthResult(AuthResult.Status.SUCCESS, response.body(), null));
                } else {

                    String errorMessage = "Login failed. ";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    authResultLiveData.setValue(new AuthResult(AuthResult.Status.ERROR, null, errorMessage));
                    errorMessageLiveData.setValue(errorMessage); // Set error message LiveData
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                String errorMessage = "Network failure.";
                authResultLiveData.setValue(new AuthResult(AuthResult.Status.ERROR, null, errorMessage));
                errorMessageLiveData.setValue(errorMessage); // Set error message LiveData
            }

        });
    }

    public void registerUser(String name, String email, String password) {
        RegistrationRequest registrationRequest = new RegistrationRequest(name, email, password);

        apiService.registerUser(registrationRequest).enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 201){

                        UserModel user = new UserModel(name, email, password, response.body().getToken());
                        authResultLiveData.setValue(new AuthResult(AuthResult.Status.SUCCESS, user, null));
                    }
                    if (response.code() == 208){
                        String errorMessage = "The email is already registered. Forgot your password? Reset it or sign in.";
                        authResultLiveData.setValue(new AuthResult(AuthResult.Status.ERROR, null, errorMessage));
                        errorMessageLiveData.setValue(errorMessage);
                    }
                } else {

                    String errorMessage = "Registration failed. ";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    authResultLiveData.setValue(new AuthResult(AuthResult.Status.ERROR, null, errorMessage));
                    errorMessageLiveData.setValue(errorMessage); // Set error message LiveData

                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                String errorMessage = "Network failure.";
                authResultLiveData.setValue(new AuthResult(AuthResult.Status.ERROR, null, errorMessage));
                errorMessageLiveData.setValue(errorMessage); // Set error message LiveData
            }
        });
    }
}


