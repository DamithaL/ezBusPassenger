package ezbus.mit20550588.passenger.data.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ezbus.mit20550588.passenger.data.model.UserModel;
import ezbus.mit20550588.passenger.data.remote.ApiServiceAuthentication;
import ezbus.mit20550588.passenger.data.remote.LoginRequest;
import ezbus.mit20550588.passenger.data.remote.RegistrationRequest;
import ezbus.mit20550588.passenger.data.remote.RetrofitClient;
import ezbus.mit20550588.passenger.data.repository.UserRepository;
import retrofit2.Call;


public class AuthViewModel extends ViewModel {
    private final UserRepository userRepository;
    private LiveData<AuthResult> authResultLiveData;

    public AuthViewModel() {
        // Create a default constructor
        this.userRepository = new UserRepository(new RetrofitClient().getClient().create(ApiServiceAuthentication.class));
        this.authResultLiveData = userRepository.getAuthResultLiveData();
    }

    // Existing constructor for dependency injection
    public AuthViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authResultLiveData = userRepository.getAuthResultLiveData();
    }

    public LiveData<AuthResult> getAuthResultLiveData() {
        return authResultLiveData;
    }

    public void loginUser(String email, String password) {
        userRepository.loginUser(email, password);
    }

    public LiveData<UserModel> getRegisteredUserLiveData() {
        return userRepository.getRegisteredUserLiveData();
    }

    public void registerUser(String name, String email, String password) {
        userRepository.registerUser(name, email, password);
    }
}
