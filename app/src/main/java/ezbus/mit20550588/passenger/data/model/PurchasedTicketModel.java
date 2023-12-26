package ezbus.mit20550588.passenger.data.model;

import static ezbus.mit20550588.passenger.util.Constants.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import ezbus.mit20550588.passenger.util.DateUtils;

@Entity(tableName = "purchased_ticket_table")
public class PurchasedTicketModel {

    @PrimaryKey(autoGenerate = true)
    private int ticketId;
   private TicketModel ticket;
    private String orderId;
    private Date purchasedDate;
    private boolean isRedeemed;
    private Date redeemedDate;

    public int getTicketId() {
        Log("PurchasedTicketModel", "1");
        return ticketId;
    }

    public TicketModel getTicket() {
        return ticket;
    }

    public String getOrderId() {
        Log("PurchasedTicketModel", "2");
        return orderId;
    }

    public Date getPurchasedDate() {
        return purchasedDate;
    }

    public String getPurchasedDateAsString() {
        return DateUtils.formatDateToString(purchasedDate);
    }

    public boolean isRedeemed() {
        Log("PurchasedTicketModel", "4");
        return isRedeemed;
    }

    public Date getRedeemedDate() {
        Log("PurchasedTicketModel", "5");
        return redeemedDate;
    }

    public String getRedeemedDateAsString() {
        return DateUtils.formatDateToString(redeemedDate);
    }
    public void setTicketId(int ticketId) {
        Log("PurchasedTicketModel", "6");
        this.ticketId = ticketId;
    }

    public void setRedeemed(boolean redeemed) {
        Log("PurchasedTicketModel", "7");
        isRedeemed = redeemed;
    }

    public void setRedeemedDate(Date redeemedDate) {
        Log("PurchasedTicketModel", "8");
        this.redeemedDate = redeemedDate;
    }

    public PurchasedTicketModel(
          TicketModel ticket,
            String orderId, Date purchasedDate, boolean isRedeemed, Date redeemedDate) {
        Log("PurchasedTicketModel", "10");
       this.ticket = ticket;
        this.orderId = orderId;
        this.purchasedDate = purchasedDate;
        this.isRedeemed = isRedeemed;
        this.redeemedDate = redeemedDate;
        Log("PurchasedTicketModel", "9");
    }
}
