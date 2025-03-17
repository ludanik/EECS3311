package org.example;

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
