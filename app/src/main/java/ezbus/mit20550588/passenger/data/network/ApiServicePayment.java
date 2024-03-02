package ezbus.mit20550588.passenger.data.network;

import java.util.List;
import java.util.Map;

import ezbus.mit20550588.passenger.data.model.PayHereRequest;
import ezbus.mit20550588.passenger.data.model.PurchasedTicketModel;
import ezbus.mit20550588.passenger.data.model.TicketOrder;
import ezbus.mit20550588.passenger.data.model.UserModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiServicePayment {

    // Base_URL is in RetrofitClient class

    @POST("/payment/initiate")
    Call<PayHereRequest> validateTicketOrder(@Body TicketOrder ticketOrder);

    @POST("/payment/notify")
    Call<Void> notifyPayment(@Body Map<String, Object> notifyPaymentRequest);

    @POST("/payment/cancel")
    Call<Void> notifyPaymentCancel(@Body Map<String, Object> notifyPaymentRequest);

    @POST("/payment/isTicketRedeemed")
    Call<TicketRedemptionStatus> isTicketRedeemed(@Body Map<String, String> checkRedeemStatusRequest);

    @POST("/payment/fetch/all-purchased-tickets")
    Call<List<PurchasedTicketModel>> fetchAllPurchasedTickets(@Body Map<String, String>  email);

}


