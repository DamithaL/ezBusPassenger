package ezbus.mit20550588.manager.data.repository;

import static ezbus.mit20550588.manager.util.Constants.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ezbus.mit20550588.manager.data.model.FleetModel;
import ezbus.mit20550588.manager.data.model.UserModel;
import ezbus.mit20550588.manager.data.network.ApiBus;
import ezbus.mit20550588.manager.data.network.ApiServiceAuthentication;
import ezbus.mit20550588.manager.data.network.LoginRequest;
import ezbus.mit20550588.manager.data.network.RegistrationRequest;
import ezbus.mit20550588.manager.data.viewModel.AuthResult;
import ezbus.mit20550588.manager.data.viewModel.CheckFleetStatusResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FleetRepository {
    private ApiBus apiService;
    private MutableLiveData<CheckFleetStatusResponse> checkStatusLiveData = new MutableLiveData<>();

    private MutableLiveData<String> regReqResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();



    public FleetRepository(ApiBus apiService) {
        this.apiService = apiService;
    }

    public LiveData<CheckFleetStatusResponse> getCheckFleetStatusLiveData() {
        return checkStatusLiveData;
    }

    public LiveData<String> getRegReqResponseLiveData() {
        return regReqResponseLiveData;
    }

    public MutableLiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }


    public void checkFleetStatus(FleetModel fleet) {
        apiService.registerFleet(fleet).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                try {
                    if (response.isSuccessful()) {

                        if (response.body() != null) {
                            checkStatusLiveData.setValue(new CheckFleetStatusResponse(response.body().toString(), response.code()));
                        }

                    } else {

                        if (response.body() != null) {
                            checkStatusLiveData.setValue(new CheckFleetStatusResponse(response.body().toString(), response.code()));
                        }

                    }
                } catch (Exception e) {
                    errorMessageLiveData.setValue(e.getMessage());
                }
            }


            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log("User repository", "onFailure", t.getMessage());
                String errorMessage = "Network failure.";
                errorMessageLiveData.setValue(errorMessage); // Set error message LiveData
            }
        });
    }

    public void registerFleet(FleetModel newFleet) {

        apiService.registerFleet(newFleet).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                try {
                    if (response.isSuccessful()) {
                        // if Bus fleet registration request received
                        if (response.body() != null) {
                            regReqResponseLiveData.setValue(response.body().toString());
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
                        errorMessageLiveData.setValue(errorMessage); // Set error message LiveData

                    }
                } catch (Exception e) {
                    errorMessageLiveData.setValue(e.getMessage());
                }
            }


            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log("User repository", "onFailure", t.getMessage());
                String errorMessage = "Network failure.";
                errorMessageLiveData.setValue(errorMessage); // Set error message LiveData
            }
        });
    }



}


