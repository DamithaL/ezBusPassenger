package ezbus.mit20550588.passenger.data.network;

import java.util.Map;

import ezbus.mit20550588.passenger.data.model.PayHereRequest;
import ezbus.mit20550588.passenger.data.model.TicketOrder;
import ezbus.mit20550588.passenger.data.model.UserModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiServicePayment {

    // Base_URL is in RetrofitClient class

    @POST("/payment/payhere/initiate")
    Call<PayHereRequest> validateTicketOrder(@Body TicketOrder ticketOrder);

    @POST("/payment/payhere/notify")
    Call<Void> notifyPayment(@Body Map<String, Object> notifyPaymentRequest);


    @POST("/payment/payhere/cancel")
    Call<Void> notifyPaymentCancel(@Body Map<String, Object> notifyPaymentRequest);
}

