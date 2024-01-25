package ezbus.mit20550588.manager.data.model;


import org.mindrot.jbcrypt.BCrypt;

import java.io.Serializable;

import ezbus.mit20550588.manager.util.UserStateManager;

public class FleetModel implements Serializable {

    private final String managerEmail;
    // If this is set as a FINAL STATIC/ FINAL and removed from the constructors,
    // that string will not be serialised and will not go to the server.
    // However it will be present in the activity as long as it is not serialised.
    private String fleetName;
    private String fleetRegistrationNumber;
    private String fleetStatus;  // Store the hashed password
    private String notes;

    // to send the fleet registration request to the server
    public FleetModel(String fleetName, String fleetRegistrationNumber, String fleetStatus) {
        this.managerEmail = UserStateManager.getInstance().getUser().getEmail();
        this.fleetName = fleetName;
        this.fleetRegistrationNumber = fleetRegistrationNumber;
        this.fleetStatus = fleetStatus;

    }

    public FleetModel(FleetModel fleet) {
        this.managerEmail = UserStateManager.getInstance().getUser().getEmail();
        this.fleetName = fleet.getFleetName();
        this.fleetRegistrationNumber = fleet.getFleetRegistrationNumber();
        this.fleetStatus = fleet.getFleetStatus();

    }


    // to get the fleet details from server
    public FleetModel(String fleetName, String fleetRegistrationNumber, String fleetStatus, String notes) {
        this.managerEmail = UserStateManager.getInstance().getUser().getEmail();
        this.fleetName = fleetName;
        this.fleetRegistrationNumber = fleetRegistrationNumber;
        this.fleetStatus = fleetStatus;
        this.notes = notes;
    }

    public String getFleetName() {
        return fleetName;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public String getFleetRegistrationNumber() {
        return fleetRegistrationNumber;
    }

    public String getFleetStatus() {
        return fleetStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setFleetName(String fleetName) {
        this.fleetName = fleetName;
    }

    public void setFleetRegistrationNumber(String fleetRegistrationNumber) {
        this.fleetRegistrationNumber = fleetRegistrationNumber;
    }

    public void setFleetStatus(String fleetStatus) {
        this.fleetStatus = fleetStatus;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "FleetModel{" +
                "managerrEmail='" + managerEmail + '\'' +
                ", fleetName='" + fleetName + '\'' +
                ", fleetRegistrationNumber='" + fleetRegistrationNumber + '\'' +
                ", fleetStatus='" + fleetStatus + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
