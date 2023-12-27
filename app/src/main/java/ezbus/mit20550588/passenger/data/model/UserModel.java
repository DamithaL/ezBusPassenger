package ezbus.mit20550588.passenger.data.model;


import org.mindrot.jbcrypt.BCrypt;

import java.io.Serializable;

public class UserModel implements Serializable {

    private final String name;
    private final String email;
    private String hashedPassword;  // Store the hashed password
    private Boolean isVerified;


    public UserModel(String name, String email, String toBeHashedPassword) {

        // Validate inputs here
        if (name == null || email == null || toBeHashedPassword == null) {
            throw new IllegalArgumentException("All fields must be provided");
        }
        this.name = name;
        this.email = email;
        this.hashedPassword = hashPassword(toBeHashedPassword);  // Hash and store the password
    }

    // to get the user details from server --- in this password will be already hashed. therefor will not hash again
    public UserModel(String name, String email, String password, Boolean isVerified) {
        this.name = name;
        this.email = email;
        this.hashedPassword = password;
        this.isVerified = isVerified;
    }


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String hashPassword(String password) {
        // Hash and salt the password using BCrypt
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public boolean checkPassword(String candidatePassword) {
        // Check if the entered password matches the hashed password
        return BCrypt.checkpw(candidatePassword, hashedPassword);
    }

    @Override
    public String toString() {
        return "UserModel{" +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", hashedPassword='" + hashedPassword + '\'' +
                '}';
    }
}
