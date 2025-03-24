package EECS3311.Models;

public enum BookingStatus {
    BOOKED,      // When a booking is initially created
    CANCELLED,   // If the booking is cancelled before the start time
    COMPLETED,   // If the booking duration has ended and checkout is complete
    EXTENDED;    // If the booking duration has been extended
}
