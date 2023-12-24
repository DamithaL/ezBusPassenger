package ezbus.mit20550588.passenger.data.network;

public interface FareCallback {
    void onFareReceived(double fare);
    void onError(String errorMessage);
}
