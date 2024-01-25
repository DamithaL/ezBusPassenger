package ezbus.mit20550588.manager.data.viewModel;

import static ezbus.mit20550588.manager.util.Constants.Log;
import static ezbus.mit20550588.manager.util.PasswordHash.hashPasswordSHA;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import ezbus.mit20550588.manager.R;
import ezbus.mit20550588.manager.data.model.FleetModel;
import ezbus.mit20550588.manager.data.model.UserModel;
import ezbus.mit20550588.manager.data.network.ApiBus;
import ezbus.mit20550588.manager.data.network.ApiServiceAuthentication;
import ezbus.mit20550588.manager.data.network.RetrofitClient;
import ezbus.mit20550588.manager.data.repository.FleetRepository;
import ezbus.mit20550588.manager.data.repository.UserRepository;
import ezbus.mit20550588.manager.util.Validator;


public class FleetViewModel extends ViewModel {
    private final FleetRepository fleetRepository;
    private LiveData<CheckFleetStatusResponse> checkFleetStatusLiveData;
    private LiveData<String> regReqResponseLiveData;
    private MutableLiveData<String> errorMessageLiveData;

    public FleetViewModel() {
        // Create a default constructor
        this.fleetRepository = new FleetRepository(new RetrofitClient().getClient().create(ApiBus.class));
        this.checkFleetStatusLiveData = fleetRepository.getCheckFleetStatusLiveData();
        this.regReqResponseLiveData = fleetRepository.getRegReqResponseLiveData();
        this.errorMessageLiveData = fleetRepository.getErrorMessageLiveData();

    }

    // Existing constructor for dependency injection
    public FleetViewModel(FleetRepository fleetRepository) {
        this.fleetRepository = fleetRepository;
        this.checkFleetStatusLiveData = fleetRepository.getCheckFleetStatusLiveData();
        this.regReqResponseLiveData = fleetRepository.getRegReqResponseLiveData();
        this.errorMessageLiveData = fleetRepository.getErrorMessageLiveData();

    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public LiveData<CheckFleetStatusResponse> getCheckFleetStatusLiveData() {
        return checkFleetStatusLiveData;
    }

    public LiveData<String> getRegReqResponseLiveData() {
        return regReqResponseLiveData;
    }

    public void checkFleetStatus(FleetModel fleet) {
        fleetRepository.checkFleetStatus(fleet);
    }

    public void registerFleet(FleetModel newFleet, TextInputEditText fleetNameTextInput, TextInputEditText fleetRegNumTextInput) {
        // Validate name, number before proceeding

        fleetNameTextInput.setError(null);
        fleetRegNumTextInput.setError(null);
        errorMessageLiveData.setValue(null);

        if (newFleet != null && !newFleet.getFleetName().isEmpty() && !newFleet.getFleetRegistrationNumber().isEmpty() && !newFleet.getManagerEmail().isEmpty()) {
            fleetRepository.registerFleet(newFleet);
        } else {
            if (fleetNameTextInput.getText().toString().isEmpty()) {
                // Empty Fields
                errorMessageLiveData.setValue("Please enter a valid name. It cannot be empty.");
                fleetNameTextInput.setError("Invalid fleet name.");
            }

            if (fleetRegNumTextInput.getText().toString().isEmpty()) {
                // Empty Fields
                errorMessageLiveData.setValue("Please enter a valid registration number. It cannot be empty.");
                fleetRegNumTextInput.setError("Invalid fleet registration number.");

            }

            if (fleetNameTextInput.getText().toString().isEmpty() && fleetRegNumTextInput.getText().toString().isEmpty()) {
                // Empty Fields
                errorMessageLiveData.setValue("Please fill in all the required fields");
                fleetNameTextInput.setError("Invalid fleet name.");
                fleetRegNumTextInput.setError("Invalid fleet registration number.");
            }
        }

    }




}
