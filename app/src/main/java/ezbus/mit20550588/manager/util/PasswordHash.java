package ezbus.mit20550588.manager.util;

import static ezbus.mit20550588.manager.util.Constants.Log;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordHash {

    public static String hashPasswordWithSalt(String password) {

        Log("hashPassword", "Called");
        // Use BCrypt's hashing function
        String hashedPassword = BCrypt.withDefaults().hashToString(10, password.toCharArray());
        Log("hashPassword", "Original Password: ", password);
        Log("hashPassword", "Hashed Password: ", hashedPassword);

        return hashedPassword;


    }

    public static boolean checkPasswordWithSalt(String candidatePassword, String hashedPassword) {
        // Use BCrypt's verifyer to check if the password matches the hash
        BCrypt.Result result = BCrypt.verifyer().verify(candidatePassword.toCharArray(), hashedPassword);
        return result.verified;
    }

    public static String hashPasswordSHA(String input) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Apply the hash function to the input string
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Handle exception (e.g., return default value or throw runtime exception)
            return null;
        }
    }
}
