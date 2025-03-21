package EECS3311.Models;

public enum UserType {
    STUDENT("Student", 5.0),
    FACULTY("Faculty", 8.0),
    STAFF("Staff", 10.0),
    VISITOR("Visitor", 15.0),
    MANAGER("Manager", 0.0),
    SUPERMANAGER("SuperManager", 0.0);

    private final String displayName;
    private final String status;
    private final double hourlyRate;

    UserType(String displayName, double hourlyRate) {
        this.displayName = displayName;
        this.hourlyRate = hourlyRate;

        if (!this.displayName.equals("Visitor") && !this.displayName.equals("Manager") && !this.displayName.equals("SuperManager")) {
            this.status = "PENDING";
        }
        else {
            this.status = "APPROVED";
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
    public String getStatus() {
        return status;
    }


    @Override
    public String toString() {
        return displayName;
    }
}
