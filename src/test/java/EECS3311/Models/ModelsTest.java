package EECS3311.Models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ModelsTest {

    private Booking booking;
    private User client;
    private ParkingSpace parkingSpace;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        client = new User("test@example.com", "securePass123", UserType.STUDENT, true);
        parkingSpace = new ParkingSpace(1, 1, ParkingStatus.OCCUPIED, 1);
        startTime = LocalDateTime.now();
        endTime = startTime.plusHours(2);
        booking = new Booking(1, client, parkingSpace, startTime, endTime,
                50, 200, "Credit Card", "ABC123", BookingStatus.COMPLETED);
    }

    @Test
    void testConstructorInitialization() {
        assertEquals(1, booking.getId());
        assertEquals(client, booking.getClient());
        assertEquals(parkingSpace, booking.getParkingSpace());
        assertEquals(startTime, booking.getStartTime());
        assertEquals(endTime, booking.getEndTime());
        assertEquals(50, booking.getDeposit());
        assertEquals(200, booking.getTotalCost());
        assertEquals("Credit Card", booking.getPaymentMethod());
        assertEquals("ABC123", booking.getLicensePlate());
        assertEquals(BookingStatus.COMPLETED, booking.getStatus());
    }

    @Test
    void testSetters() {
        User newClient = new User("test2@example.com", "securePass123", UserType.STUDENT, true);
        ParkingSpace newParkingSpace = new ParkingSpace(10, 25, ParkingStatus.AVAILABLE, 50);
        LocalDateTime newStartTime = startTime.plusDays(1);
        LocalDateTime newEndTime = newStartTime.plusHours(3);

        booking.setId(2);
        booking.setClient(newClient);
        booking.setParkingSpace(newParkingSpace);
        booking.setDeposit(100);
        booking.setTotalCost(300);
        booking.setPaymentMethod("Debit Card");
        booking.setLicensePlate("XYZ789");
        booking.setStatus(BookingStatus.CANCELLED);

        assertEquals(2, booking.getId());
        assertEquals(newClient, booking.getClient());
        assertEquals(newParkingSpace, booking.getParkingSpace());
        assertEquals(100, booking.getDeposit());
        assertEquals(300, booking.getTotalCost());
        assertEquals("Debit Card", booking.getPaymentMethod());
        assertEquals("XYZ789", booking.getLicensePlate());
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    void testBookingDepositCannotBeNegative() {
        booking.setDeposit(-50);
        assertTrue(booking.getDeposit() >= 0, "Deposit should not be negative.");
    }

    @Test
    void testBookingTotalCostCannotBeNegative() {
        booking.setTotalCost(-200);
        assertTrue(booking.getTotalCost() >= 0, "Total cost should not be negative.");
    }

    @Test
    void testUserEmailFormat() {
        assertTrue(client.getEmail().contains("@"), "Email should contain '@' symbol.");
    }

    @Test
    void testBookingStatusChange() {
        booking.setStatus(BookingStatus.CANCELLED);
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    void testUserValidationStatus() {
        assertTrue(client.isPendingValidation());
        client.setPendingValidation(false);
        assertFalse(client.isPendingValidation());
    }

    @Test
    void testParkingSpaceStatusChange() {
        parkingSpace = new ParkingSpace(2, 10, ParkingStatus.AVAILABLE, 100);
        assertEquals(ParkingStatus.AVAILABLE, parkingSpace.getStatus());
    }

    @Test
    void testBookingPaymentMethodChange() {
        booking.setPaymentMethod("PayPal");
        assertEquals("PayPal", booking.getPaymentMethod());
    }

    @Test
    void testBookingStartTimeBeforeEndTime() {
        assertTrue(booking.getStartTime().isBefore(booking.getEndTime()), "Start time should be before end time.");
    }
}
