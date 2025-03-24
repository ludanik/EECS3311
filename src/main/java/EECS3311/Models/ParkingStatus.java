package EECS3311.Models;

public enum ParkingStatus {
    AVAILABLE,   // When the parking space is free to be booked
    BOOKED,      // When the space is reserved for a user
    OCCUPIED,    // When a car is physically in the space (as detected by a sensor)
    MAINTENANCE; // When the space is disabled for maintenance
}
