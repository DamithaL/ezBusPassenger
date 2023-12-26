package ezbus.mit20550588.passenger.data.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

import ezbus.mit20550588.passenger.data.repository.UserRepository;
import ezbus.mit20550588.passenger.util.UserStateManager;

public class TicketOrder implements Serializable {
    private final TicketModel ticket;
    private final String userEmail;
    // If this is set as a FINAL STATIC/ FINAL and removed from the constructors,
    // that string will not be serialised and will not go to the server.
    // However it will be present in the activity as long as it is not serialised.
    private String orderId;
    private Date purchasedDate;
    private boolean isRedeemed;
    private String busId; // when the ticket is redeemed
    private Date redeemedDate;

    public TicketOrder(
            TicketModel ticket,
            String orderId,
            Date purchasedDate,
            boolean isRedeemed,
            String busId,
            Date redeemedDate
    ) {
        this.ticket = ticket;
        this.userEmail = UserStateManager.getInstance().getUser().getEmail();
        this.orderId = orderId;
        this.purchasedDate = purchasedDate;
        this.isRedeemed = isRedeemed;
        this.busId = busId;
        this.redeemedDate = redeemedDate;
    }

    // Constructor for the ticket oder that will be sent to the server to validated
    public TicketOrder(
            TicketModel ticket
    ) {
        this.ticket = ticket;
        this.userEmail = UserStateManager.getInstance().getUser().getEmail();
    }
    public TicketModel getTicket() {
        return ticket;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getOrderId() {
        return orderId;
    }

    public Date getPurchasedDate() {
        return purchasedDate;
    }

    public boolean isRedeemed() {
        return isRedeemed;
    }

    public String getBusId() {
        return busId;
    }

    public Date getRedeemedDate() {
        return redeemedDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "TicketOrder{" +
                "ticket=" + ticket +
                ", userEmail='" + userEmail + '\'' +
                ", orderId='" + orderId + '\'' +
                ", purchasedDate=" + purchasedDate +
                ", isRedeemed=" + isRedeemed +
                ", busId='" + busId + '\'' +
                ", redeemedDate=" + redeemedDate +
                '}';
    }
}
