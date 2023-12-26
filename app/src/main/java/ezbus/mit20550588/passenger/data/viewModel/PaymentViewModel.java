package ezbus.mit20550588.passenger.data.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

import ezbus.mit20550588.passenger.data.model.PayHereRequest;
import ezbus.mit20550588.passenger.data.model.TicketOrder;
import ezbus.mit20550588.passenger.data.network.ApiServiceBus;
import ezbus.mit20550588.passenger.data.network.ApiServicePayment;
import ezbus.mit20550588.passenger.data.network.PaymentStatusResponse;
import ezbus.mit20550588.passenger.data.network.RetrofitClient;
import ezbus.mit20550588.passenger.data.repository.PaymentRepository;
import ezbus.mit20550588.passenger.data.repository.TicketRepository;

public class PaymentViewModel extends ViewModel {

    private PaymentRepository paymentRepository;
    private LiveData<String> errorLiveData;

    private LiveData<PayHereRequest> ticketOrderLiveData;

    private LiveData<PaymentStatusResponse> paymentStatusLiveData;

    // Constructor to initialize the repository
    public PaymentViewModel() {
        this.paymentRepository = new PaymentRepository(new RetrofitClient().getClient().create(ApiServicePayment.class));
        this.ticketOrderLiveData = paymentRepository.getTicketOrderLiveData();
        this.errorLiveData = paymentRepository.getErrorLiveData();
        this.ticketOrderLiveData = paymentRepository.getTicketOrderLiveData();
        this.paymentStatusLiveData = paymentRepository.getPaymentStatusLiveData();
    }

    // Existing constructor for dependency injection
    public PaymentViewModel(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        this.ticketOrderLiveData = paymentRepository.getTicketOrderLiveData();
        this.errorLiveData = paymentRepository.getErrorLiveData();
        this.paymentStatusLiveData = paymentRepository.getPaymentStatusLiveData();
    }


    public LiveData<PayHereRequest> getTicketOrderLiveData() {
        return ticketOrderLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<PaymentStatusResponse> getPaymentStatusLiveData() {
        return paymentStatusLiveData;
    }

    public void validateTicketOrder(TicketOrder ticket) {

        paymentRepository.validateTicketOrder(ticket);
    }

    public void notifyPayment(Map<String, Object> notifyPaymentRequest) {
        paymentRepository.notifyPayment(notifyPaymentRequest);
    }

    public void notifyPaymentCancel(Map<String, Object> notifyPaymentRequest) {
        paymentRepository.notifyPaymentCancel(notifyPaymentRequest);
    }


}
