package ezbus.mit20550588.manager.data.viewModel;

import ezbus.mit20550588.manager.data.model.UserModel;

public class CheckFleetStatusResponse {

    private final String status;
    private final int responseCode;

    public CheckFleetStatusResponse(String status, int responseCode) {
        this.status = status;
        this.responseCode = responseCode;
    }

    public String getStatus() {
        return status;
    }

    public int getResponseCode() {
        return responseCode;
    }
}

