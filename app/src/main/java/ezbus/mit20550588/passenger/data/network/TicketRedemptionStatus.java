package ezbus.mit20550588.passenger.data.network;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class TicketRedemptionStatus {
    @SerializedName("valid")
    private boolean isValid;

    @SerializedName("message")
    private String message;

    @SerializedName("redeemedDate")
    private Date redeemedDate;

    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }

    public Date getRedeemedDate() {
        return redeemedDate;
    }

    @Override
    public String toString() {
        return "TicketRedemptionStatus{" +
                "isValid=" + isValid +
                ", message='" + message + '\'' +
                ", redeemedDate='" + redeemedDate + '\'' +
                '}';
    }
}
