package org.example;

// User model
enum UserType {
    STUDENT("Student", 5.0),
    FACULTY("Faculty", 8.0),
    STAFF("Staff", 10.0),
    VISITOR("Visitor", 15.0),
    MANAGER("Manager", 0.0);

    private final String displayName;
    private final double hourlyRate;

    UserType(String displayName, double hourlyRate) {
        this.displayName = displayName;
        this.hourlyRate = hourlyRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

class User {
    private String email;
    private String password;
    private UserType userType;
    private boolean pendingValidation;

    public User(String email, String password, UserType userType) {
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.pendingValidation = (userType != UserType.VISITOR);
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserType getUserType() {
        return userType;
    }

    public boolean isPendingValidation() {
        return pendingValidation;
    }

    public void setPendingValidation(boolean pendingValidation) {
        this.pendingValidation = pendingValidation;
    }
}