package ezbus.mit20550588.passenger.data.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

import ezbus.mit20550588.passenger.util.UserStateManager;

public class PayHereRequest implements Serializable {
    private String merchantId;
    private final static String currency = "LKR";
    // This is set as a FINAL STATIC/ FINAL and removed from the constructors,
    // so that string will not be serialised and will not go to the server.
    // However it will be present in the activity as long as it is not serialised.

    private double amount;
    private String orderId;
    private String itemsDescription;
    private String custom1;
    private String custom2;
    private final static String firstName =  UserStateManager.getInstance().getUser().getName();
    private String lastName;
    private final static  String email = UserStateManager.getInstance().getUser().getEmail();
    private String phone;
    private String address;
    private String city;
    private final static String  country = "Sri Lanka";

    public PayHereRequest(
            String merchantId,
            double amount,
            String orderId,
            String itemsDescription,
            String custom1,
            String custom2,
            String lastName,
            String phone,
            String address,
            String city
    ) {
        this.merchantId = merchantId;
        this.amount = amount;
        this.orderId = orderId;
        this.itemsDescription = itemsDescription;
        this.custom1 = "";
        this.custom2 = "";
        this.lastName = "";
        this.phone = "";
        this.address = "";
        this.city = "";
    }

    public String getMerchantId() {
        return merchantId;
    }

    public double getAmount() {
        return amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getItemsDescription() {
        return itemsDescription;
    }

    public String getCustom1() {
        return custom1;
    }

    public String getCustom2() {
        return custom2;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCountry() {
        return country;
    }

    @NonNull
    @Override
    public String toString() {
        return "PayHereRequest{" +
                "merchantId='" + merchantId + '\'' +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                ", orderId='" + orderId + '\'' +
                ", itemsDescription='" + itemsDescription + '\'' +
                ", custom1='" + custom1 + '\'' +
                ", custom2='" + custom2 + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
