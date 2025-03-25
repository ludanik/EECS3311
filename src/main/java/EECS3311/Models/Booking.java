package EECS3311.Models;
import java.time.LocalDateTime;

public class Booking {
    private int id;
    private User client;
    private ParkingSpace parkingSpace;
    private double startTime;
    private int hours;
    private double deposit;
    private double totalCost;
    private int extendedHours;
    private String paymentMethod;
    private BookingStatus status;

    public Booking(int id, User client, ParkingSpace parkingSpace, double startTime, int hours, double deposit, double totalCost, int extendedHours, String paymentMethod, BookingStatus status) {
        this.id = id;
        this.client = client;
        this.parkingSpace = parkingSpace;
        this.startTime = startTime;
        this.hours = hours;
        this.deposit = deposit;
        this.totalCost = totalCost;
        this.extendedHours = extendedHours;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public ParkingSpace getParkingSpace() {
        return parkingSpace;
    }

    public void setParkingSpace(ParkingSpace parkingSpace) {
        this.parkingSpace = parkingSpace;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public int getExtendedHours() {
        return extendedHours;
    }

    public void setExtendedHours(int extendedHours) {
        this.extendedHours = extendedHours;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
