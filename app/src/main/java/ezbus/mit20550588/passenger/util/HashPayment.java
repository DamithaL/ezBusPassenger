package ezbus.mit20550588.passenger.util;

import static ezbus.mit20550588.passenger.util.Constants.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
public class HashPayment {
    public static void main(String[] args) {
        // This is just an example; you won't be able to run this in an Android app.
        // You should call generateHash from within your Android app.
    }

    public static String generateHash(String merchantID, String merchantSecret, String orderID,
                                      double amount, String currency) {
        DecimalFormat df = new DecimalFormat("0.00");
        String amountFormatted = df.format(amount);

        String input = merchantID + orderID + amountFormatted + currency + getMd5(merchantSecret);

        String hash = getMd5(input);
        Log("Generated Hash", hash); // Log the hash for verification or debugging

        return hash;
    }

    private static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext.toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}