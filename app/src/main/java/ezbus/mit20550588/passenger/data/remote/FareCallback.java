package ezbus.mit20550588.passenger.data.remote;

public interface FareCallback {
    void onFareReceived(double fare);
    void onError(String errorMessage);
}
