package EECS3311.Models;
import java.time.LocalDateTime;

public class Booking {
    private int id;
    private User client;
    private ParkingSpace parkingSpace;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double payment;
}
